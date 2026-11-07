import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Monsters and Heroes
 * Main game class implementing the Game Interface
 * Combines all components to manage the state of the game such as:
 * Moving around the map
 * Generating battles with some probability
 * Checking if the game is over
 * Allowing heroes to enter a market if they're on a market tile.
 */

public class MonstersandHeroes implements Game {
    private final HeroGroup heroes;
    private MonsterGroup monsters;
    private Position currentPosition;
    private Grid map;
    private final Controller console;
    private boolean inBattle;
    private final double battleProbability;

    private static final Random rand = new Random();

    private final ItemFactory itemFactory = new ItemFactory();
    private final MarketFactory marketFactory = new MarketFactory(itemFactory);
    private final Map<String, Market> markets = new HashMap<>();
    private final InventoryMenu inventoryMenu;

    public MonstersandHeroes(double battleProbability) {
        if (battleProbability < 0.0 || battleProbability > 1.0) {
            throw new IllegalArgumentException("Probability must be between 0.0 and 1.0.");
        }

        this.currentPosition = new Position(0, 0);
        this.heroes = new HeroGroup();
        this.monsters = new MonsterGroup();
        this.console = new Controller(new HeroFactory());
        this.inventoryMenu = new InventoryMenu(console);
        this.inBattle = false;
        this.battleProbability = battleProbability;
    }

    public boolean isInBattle() {
        return rand.nextDouble() < battleProbability;
    }

    @Override
    public void start() {
        // Welcome message
        console.displayWelcome();

        // Gather the number of heroes and their selections
        int numHeroes = console.getHeroCount();
        List<Hero> heroList = console.selectHero(numHeroes);

        // Load the selected heroes into the party
        for (Hero hero : heroList) {
            heroes.addHero(hero);
        }

        // Get the map size and generate the map
        int dim = console.askDimensions();
        map = new Grid(dim);
    }

    @Override
    public void play() {
        start();

        while (true) {
            map.displayGrid(currentPosition);
            handleInput(console.getKeyPress());

            if (map.getSpace(currentPosition).getSpaceType() == SpaceType.COMMON
                    && !isInStartPosition()) {

                // Generate a battle with the configured probability
                if (isInBattle()) {
                    System.out.println(
                            "You've encountered monsters! Starting a battle..."
                    );

                    inBattle = true;
                    startBattle();
                } else {
                    System.out.println(
                            "No monsters found here. Keep moving!"
                    );
                }
            }
        }
    }

    @Override
    public void end() {
        console.quitGameText(heroes);
        System.exit(0);
    }

    // Main choices for a user to take at each iteration
    private void handleInput(char input) {
        switch (Character.toUpperCase(input)) {
            case 'W' -> move(Direction.UP);
            case 'A' -> move(Direction.LEFT);
            case 'S' -> move(Direction.DOWN);
            case 'D' -> move(Direction.RIGHT);
            case 'Q' -> end();
            case 'I' -> heroes.displayHeroGroupInformation();
            case 'M' -> enterMarket();
            case 'V' -> manageInventory();
            default -> System.out.println("Invalid input.");
        }
    }

    // starts the battle betwene the monsters and heroes
    private void startBattle() {
        monsters = monsters.createRandomMonsterGroup(heroes);

        Battle battle = new Battle(
                monsters,
                heroes,
                console,
                () -> map.displayGrid(currentPosition)
        );

        monsters.displayMonsterGroupInformation();
        inBattle = true;

        while (inBattle) {
            battle.heroesTurn();

            // Check whether all monsters have fainted
            if (battle.isMonstersFainted()) {
                break;
            }

            battle.monsterTurn();

            // Check whether all heroes have fainted
            if (battle.isHeroesFainted()) {
                break;
            }

            // Regenerate HP and mana for living heroes
            battle.endOfRound();
        }

        inBattle = false;

        if (battle.isMonstersFainted()) {
            System.out.println("Heroes have won the battle!");

            battle.awardRewards();
            battle.reviveFaintedHeroes();
            heroes.levelUpHeroes();
        } else {
            console.showGameOver(heroes);
            end();
        }
    }

    private void move(Direction direction) {
        // Calculate the destination coordinates
        int newRow = currentPosition.getRow() + direction.getRowChange();
        int newCol = currentPosition.getCol() + direction.getColChange();

        // Check whether the destination is within the grid
        if (newRow < 0 || newRow >= map.getDim()
                || newCol < 0 || newCol >= map.getDim()) {

            System.out.println("You cannot move outside the grid!");
            return;
        }

        // Create the destination position after validating boundaries
        Position newPosition = new Position(newRow, newCol);

        // Retrieve the destination space
        Space destination = map.getSpace(newPosition);

        // Check whether the destination is accessible
        if (!destination.getSpaceType().isAccessible()) {
            System.out.println("You cannot move into an inaccessible space!");
            return;
        }

        // Update the party's position
        currentPosition = newPosition;

        System.out.println("You moved successfully!");
    }

    private Market getMarketAtCurrentPosition() {
        String key = currentPosition.getRow() + "," + currentPosition.getCol();

        return markets.computeIfAbsent(
                key,
                k -> marketFactory.createMarket()
        );
    }

    public void enterMarket() {
        if (map.getSpace(currentPosition).getSpaceType() != SpaceType.MARKET) {
            System.out.println(
                    "You must be on a market space to enter the market!"
            );
            return;
        }

        // Same market every visit to this tile
        Market market = getMarketAtCurrentPosition();

        // Heroes shop one at a time
        for (Hero customer : heroes.getHeroes()) {
            console.displayMarketMenu(customer, market);
        }
    }

    private boolean isInStartPosition() {
        return currentPosition.getRow() == 0
                && currentPosition.getCol() == 0;
    }

    private void manageInventory() {
        // Skip the hero prompt when there is only one hero
        int index = heroes.getNumHeroes() == 1
                ? 0
                : console.selectHero(heroes);

        Hero hero = heroes.getHeroByIndex(index);

        boolean open = true;

        while (open) {
            switch (console.getInventoryChoice(hero)) {
                case 'W':
                    inventoryMenu.equipWeapon(hero);
                    break;

                case 'E':
                    inventoryMenu.equipArmor(hero);
                    break;

                case 'P':
                    inventoryMenu.usePotion(hero);
                    break;

                case 'U':
                    inventoryMenu.unequipWeapon(hero);
                    break;

                case 'R':
                    inventoryMenu.unequipArmor(hero);
                    break;

                case 'V':
                    System.out.println(hero.getBackpack());
                    break;

                case 'B':
                    open = false;
                    break;
            }
        }
    }
}
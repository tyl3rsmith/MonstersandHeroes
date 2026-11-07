import java.util.*;

public class MonstersandHeroes implements Game {
    private HeroGroup heroes;
    private MonsterGroup monsters;
    private Position currentPosition;
    private Grid map;
    private Controller console;
    private boolean inBattle;
    private final double battleProbability;
    private static final Random rand = new Random();
    private final ItemFactory itemFactory = new ItemFactory();          // loads the files once
    private final MarketFactory marketFactory = new MarketFactory(itemFactory);
    private final Map<String, Market> markets = new HashMap<>();
    private final InventoryMenu inventoryMenu;


    public MonstersandHeroes(double battleProbability) {
        if (battleProbability < 0.0 || battleProbability > 1.0) {
            throw new IllegalArgumentException("Probability must be between 0.0 and 1.0");
        }

        this.currentPosition = new Position(0, 0);
        this.heroes = new HeroGroup();
        this.monsters = new MonsterGroup();
        this.console = new Controller();
        this.inventoryMenu = new InventoryMenu(console);
        this.inBattle = false;
        this.battleProbability = battleProbability;
    }

    public boolean isInBattle() {
        // probability of a battle occurring is battleProbability
        // a battle does not occur with probability 1 - battleProbability
        return rand.nextDouble() < battleProbability;
    }

    @Override
    public void start() {
        // welcome message
        console.displayWelcome();

        // gather number of heroes and the hero choices from the user
        int numHeroes = console.getHeroCount();
        List<Hero> heroList = console.selectHero(numHeroes);

        // load hero party into HeroGroup
        for (Hero hero : heroList) {
            this.heroes.addHero(hero);
        }

        // get the map size and generate the map
        int dim = console.askDimensions();
        map = new Grid(dim);

    }

    @Override
    public void play() {
        start();

        while (true) {
            map.displayGrid(currentPosition);
            handleInput(console.getKeyPress());

            int row = currentPosition.getRow();
            int col = currentPosition.getCol();

            if (map.getSpace(currentPosition).getSpaceType() == SpaceType.COMMON && !isInStartPosition()) {
                // generate a battle with some probability
                if (isInBattle()) {
                    System.out.println("You've encountered monsters starting a battle");
                    this.inBattle = true;
                    startBattle();
                } else {
                    System.out.println("No monster's found here keep moving!");
                }
            } else if (map.getSpace(currentPosition).getSpaceType() == SpaceType.MARKET && !isInStartPosition()) {
                // ask the user if they'd like to enter the market
            }
        }
    }

    @Override
    public void end() {
        console.quitGame(heroes);
        System.exit(0);
    }

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

    private void startBattle() {
        monsters = monsters.createRandomMonsterGroup(heroes);
        Battle battle = new Battle(monsters, heroes, () -> map.displayGrid(currentPosition)); // callback provided so we can use it inside battle class
        monsters.displayMonsterGroupInformation();
        inBattle = true;

        while (inBattle) {
            battle.heroesTurn();                 // every living hero acts

            if (battle.isMonstersFainted()) {    // check before monsters get a turn
                break;
            }

            battle.monsterTurn();                // every monster acts

            if (battle.isHeroesFainted()) {
                break;
            }

            battle.endOfRound();                 // regen for living heroes only
        }

        inBattle = false;

        if (battle.isMonstersFainted()) {
            System.out.println("Heroes have won the battle!");
            battle.awardRewards();               // gold + XP, living heroes only
            battle.reviveFaintedHeroes();        // after rewards, so fainted heroes get none
            heroes.levelUpHeroes();
        } else {
            console.showGameOver(heroes);
            end();
        }
    }

    private void move(Direction direction) {

        // Calculate the destination coordinates.
        int newRow = currentPosition.getRow() + direction.getRowChange();
        int newCol = currentPosition.getCol() + direction.getColChange();

        // Check whether the destination is within the grid.
        if (newRow < 0 || newRow >= map.getDim() || newCol < 0 || newCol >= map.getDim()) {

            System.out.println("You cannot move outside the grid!");
            return;
        }

        // Create the destination position after validating the boundaries.
        Position newPosition = new Position(newRow, newCol);

        // Retrieve the destination space.
        Space destination = map.getSpace(newPosition);

        // Check whether the destination is accessible.
        if (!destination.getSpaceType().isAccessible()) {
            System.out.println("You cannot move into an inaccessible space!");
            return;
        }

        // Movement is valid, so update the party's position.
        currentPosition = newPosition;

        System.out.println("You moved successfully!");
    }

    private Market getMarketAtCurrentPosition() {
        String key = currentPosition.getRow() + "," + currentPosition.getCol();
        return markets.computeIfAbsent(key, k -> marketFactory.createMarket());
    }

    public void enterMarket() {
        if (map.getSpace(currentPosition).getSpaceType() != SpaceType.MARKET) {
            System.out.println("You must be on a market space to enter the market!");
            return;
        }

        // same market every visit to this tile, created the first time it's entered
        Market market = getMarketAtCurrentPosition();

        // heroes shop one at a time; each has their own gold and inventory
        for (Hero customer : heroes.getHeroes()) {
            console.displayMarketMenu(customer, market);

        }
    }

    private boolean isInStartPosition() {
        return currentPosition.getRow() == 0 && currentPosition.getCol() == 0;
    }

    private void manageInventory() {
        // skip the hero prompt when there's only one hero
        int index = heroes.getNumHeroes() == 1 ? 0 : console.selectHero(heroes);
        Hero hero = heroes.getHeroByIndex(index);

        boolean open = true;
        while (open) {
            switch (console.getInventoryChoice(hero)) {
                case 'W': inventoryMenu.equipWeapon(hero); break;
                case 'E': inventoryMenu.equipArmor(hero); break;
                case 'P': inventoryMenu.usePotion(hero); break;
                case 'U': inventoryMenu.unequipWeapon(hero); break;
                case 'R': inventoryMenu.unequipArmor(hero); break;
                case 'V': System.out.println(hero.getBackpack()); break;
                case 'B': open = false; break;
            }
        }
    }

}

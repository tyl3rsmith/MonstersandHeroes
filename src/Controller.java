import java.util.*;
import java.util.function.Function;

/**
 * Controller is the game's only console interface. It prints menus and screens and reads and validates player input.
 * It contains no game rules and does no file reading: the game loop, battle, market, and inventory classes decide what happens,
 * HeroFactory supplies the heroes to choose from, and Controller only asks the player and returns the answer.
 */

public class Controller {
    private final Scanner scanner = new Scanner(System.in);
    private final HeroFactory heroFactory;

    public Controller(HeroFactory heroFactory) {
        this.heroFactory = heroFactory;
    }

    // 1.
    // Input helpers: every read goes through nextLine(), so no newline is
    // ever left behind in the buffer and empty input can't crash charAt(0).

    // returns the first character of a non-empty line, uppercased
    private char readChar() {
        while (true) {
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return Character.toUpperCase(line.charAt(0));
            }
            System.out.print("Please enter a choice: ");
        }
    }

    // reads a single character and re-prompts until it is one of validChars
    private char readChoice(String validChars, String errorMessage) {
        char choice = readChar();
        while (validChars.indexOf(choice) == -1) {
            System.out.println(errorMessage);
            choice = readChar();
        }
        return choice;
    }

    // returns an int in min, max inclusive, re-prompting until a valid input is given
    private int readInt(int min, int max) {
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // needed incase a number isn't given and this exception is thrown
                // simply just handled by reprompting until a valid number is given
            }
            System.out.print("Enter a number between " + min + " and " + max + ": ");
        }
    }

    // 2.
    // Welcome / setup

    public void displayWelcome() {
        System.out.println("==============================================");
        System.out.println("          LEGENDS: MONSTERS AND HEROES");
        System.out.println("==============================================");
        System.out.println();
        System.out.println("Welcome, adventurer!");
        System.out.println();
        System.out.println("Your journey begins in a world filled with");
        System.out.println("dangerous monsters, valuable treasures, and");
        System.out.println("mysterious markets.");
        System.out.println();
        System.out.println("YOUR OBJECTIVE:");
        System.out.println("Explore the world with your party of heroes,");
        System.out.println("defeat monsters, earn gold and experience,");
        System.out.println("buy powerful items, and level up as far as you can.");
        System.out.println();
        System.out.println("EXPLORING (type a letter, then press Enter):");
        System.out.println("  W - Move up");
        System.out.println("  A - Move left");
        System.out.println("  S - Move down");
        System.out.println("  D - Move right");
        System.out.println("  I - View hero information");
        System.out.println("  V - Manage inventory (equip gear, use potions)");
        System.out.println("  M - Enter a market (when on a market space)");
        System.out.println("  Q - Quit the game (works at any time)");
        System.out.println();
        System.out.println("THE WORLD:");
        System.out.println("  [ ] Common space: You may run into monsters.");
        System.out.println("  [M] Market: Buy, sell, and repair items.");
        System.out.println("  [X] Inaccessible: You cannot enter these spaces.");
        System.out.println("  [P] Your party's current location.");
        System.out.println();
        System.out.println("BATTLES:");
        System.out.println("  Every hero acts, then every monster acts, until one side is out.");
        System.out.println("  On a hero's turn:");
        System.out.println("    A - Attack with the equipped weapon (bare fists if none)");
        System.out.println("    S - Cast a spell (uses mana, and the spell is used up)");
        System.out.println("    P - Use a potion (used up)");
        System.out.println("    W / E - Equip a weapon / a piece of armor");
        System.out.println("    I - Show statistics     M - Show the map     Q - Quit");
        System.out.println("  Looking at statistics or the map does not use up your turn.");
        System.out.println("  Heroes and monsters can dodge. Armor reduces damage taken.");
        System.out.println("  A hero who faints sits out the battle. If you win, they come");
        System.out.println("  back with half their health and mana, but earn no rewards.");
        System.out.println("  If every hero faints, the game is over.");
        System.out.println();
        System.out.println("MARKETS AND GEAR:");
        System.out.println("  Each hero has their own gold and backpack.");
        System.out.println("  B - Buy (you need enough gold and a high enough level)");
        System.out.println("  S - Sell (you get half the price back)");
        System.out.println("  R - Repair (half the price)");
        System.out.println("  Weapons and armor wear out. A broken piece is unequipped and");
        System.out.println("  must be repaired before it can be used again.");
        System.out.println("  Gear must be unequipped (inventory menu) before it can be");
        System.out.println("  sold or repaired.");
        System.out.println();
        System.out.println("Good luck, adventurer. Your legend awaits!");
        System.out.println("==============================================");
        System.out.println();
    }

    public int askDimensions() {
        System.out.println("Please choose a world size between 6 and 10: ");
        return readInt(6, 10);
    }

    public int getHeroCount() {
        System.out.println("How many heroes would you like to play with?");
        System.out.println("Please enter a number between 1 and " + HeroGroup.MAX_HERO_GROUP_SIZE + ":");
        return readInt(1, HeroGroup.MAX_HERO_GROUP_SIZE);
    }

    // 3.
    // Exploration/Movement and Hero

    // Main action prompt used in the game when user is moving around the board
    public char getKeyPress() {
        System.out.println("Enter a command:");
        System.out.println("  W - Move up");
        System.out.println("  A - Move left");
        System.out.println("  S - Move down");
        System.out.println("  D - Move right");
        System.out.println("  I - Show hero information");
        System.out.println("  M - Enter market");
        System.out.println("  V - Manage inventory");
        System.out.println("  Q - Quit game");
        System.out.print("\nYour choice: ");

        return readChoice("WASDIMQV", "Enter one of: W, A, S, D, I, M, V, Q: ");
    }

    // helper method for choosing heroes when forming a hero group
    private Hero chooseHero(HeroType type, Set<String> selectedNames) {
        List<Hero> options = heroFactory.getAvailableHeroes(type, selectedNames);
        String label = type.name().toLowerCase();

        if (options.isEmpty()) {
            System.out.println("No more available " + label + "s.");
            return null;
        }

        return selectItem("\n========== " + type.name() + " OPTIONS ==========", options,
                h -> h.getName()
                        + " | Mana: " + (int) h.getMana()
                        + " | Strength: " + (int) h.getStrength()
                        + " | Agility: " + (int) h.getAgility()
                        + " | Dexterity: " + (int) h.getDexterity()
                        + " | Starting Gold: " + (int) h.getMoney()
                        + " | Starting XP: " + h.getXp());
    }

    // returns the zero-based index of the chosen hero, or -1 if the party is empty
    public int selectHero(HeroGroup party) {
        if (party.getNumHeroes() < 1) {
            return -1;
        }

        party.displayHeroGroupInformation();
        System.out.println("Select a hero you'd like to use: (1 to " + party.getNumHeroes() + "):");
        return readInt(1, party.getNumHeroes()) - 1;
    }

    // main method used to form the hero group
    public List<Hero> selectHero(int heroCount) {
        List<Hero> selectedHeroes = new ArrayList<>();
        Set<String> selectedNames = new HashSet<>(); // needed so heroes are unique

        for (int i = 0; i < heroCount; i++) {
            System.out.println("\nSelect a hero type:");
            System.out.println("W - Warrior");
            System.out.println("S - Sorcerer");
            System.out.println("P - Paladin");

            char heroChoice = readChoice("WSP", "Invalid choice. Enter W, S, or P:");

            HeroType type = switch (heroChoice) {
                case 'W' -> HeroType.WARRIOR;
                case 'S' -> HeroType.SORCERER;
                case 'P' -> HeroType.PALADIN;
                default -> throw new IllegalStateException("Unexpected hero choice: " + heroChoice);
            };

            Hero selectedHero = chooseHero(type, selectedNames);

            if (selectedHero != null) {
                selectedHeroes.add(selectedHero);
                selectedNames.add(selectedHero.getName());
            } else {
                // cancelled, or no heroes of that type left player must pick a type again
                i--;
            }
        }

        return selectedHeroes;
    }

    // 4.
    // Battle

    // main method for getting a hero's choice of action during a battle
    public char getHeroBattleChoice(Hero hero) {
        System.out.println("\n========== HEROES TURN ==========");
        System.out.println("Enter a command:");
        System.out.println("  A - Attack, using " + hero.getName() + "'s equipped weapon");
        System.out.println("  S - Cast a spell from " + hero.getName() + "'s inventory");
        System.out.println("  P - Use a potion from " + hero.getName() + "'s inventory");
        System.out.println("  W - Equip a weapon");
        System.out.println("  E - Equip a piece of armor");
        System.out.println("  I - Show statistics");
        System.out.println("  M - Show the map");
        System.out.println("  Q - Quit the game");

        return readChoice("ASPWEIMQ", "Invalid choice. Enter A, S, P, W, E, I, M or Q:");
    }

    // returns the zero-based index of the chosen monster, or -1 if there are none
    public int selectMonster(MonsterGroup monsters) {
        if (monsters.getNumMonsters() < 1) {
            return -1;
        }

        monsters.displayMonsterGroupInformation();
        System.out.println("Select your desired target monster (1 to " + monsters.getNumMonsters() + "):");
        return readInt(1, monsters.getNumMonsters()) - 1;
    }

    // displays stats for monster or hero depending on the user's choice
    public void statistics(MonsterGroup monsterGroup, HeroGroup heroGroup) {
        System.out.println("Would you like to see monster or hero statistics?");
        System.out.println("Enter 'M' for Monster or 'H' for Hero Statistics");

        char choice = readChoice("MH",
                "Invalid character entered please enter 'M' for Monster or 'H' for Hero Statistics");

        switch (choice) {
            case 'M':
                monsterGroup.displayMonsterGroupInformation();
                break;
            case 'H':
                heroGroup.displayBattleInformation();
                break;
        }
    }

    // 5.
    // Market

    // Main menu displayed while a hero is shopping at a market, repeats until they leave i.e. enter 'L'
    public void displayMarketMenu(Hero hero, Market market) {
        System.out.println("\nWelcome to the market " + hero.getName() + "!");

        boolean shopping = true;
        while (shopping) {
            System.out.println("\n" + hero.getName() + " | Level " + hero.getLevel()
                    + " | Gold: " + (int) hero.getMoney());

            System.out.println("  B - Buy   S - Sell   R - Repair   L - Leave");

            switch (readChoice("BSRL", "Enter B, S, R, or L:")) {
                case 'B': handleBuy(hero, market); break;
                case 'S': handleSell(hero, market); break;
                case 'R': handleRepair(hero, market); break;
                case 'L': shopping = false; break;
            }
        }
    }

    // Repair prompt: only prints prompt for items which are damaged
    private void handleRepair(Hero hero, Market market) {
        List<Gear> damaged = new ArrayList<>();
        for (Gear gear : hero.getBackpack().getItemsOfType(Gear.class)) {
            if (gear.needsRepair()) {
                damaged.add(gear);
            }
        }

        if (damaged.isEmpty()) {
            System.out.println("Nothing in " + hero.getName() + "'s backpack needs repair."
                    + " (Equipped gear has to be unequipped first.)");
            return;
        }

        Gear gear = selectItem("Choose an item to repair:", damaged,
                g -> g + " -> repair costs " + (int) g.getRepairCost() + " gold");
        if (gear != null) {
            System.out.println(market.repairItem(hero, gear).getMessage());
        }
    }

    // gets all items a market has, and prompts a hero to choose the item they want to buy
    private void handleBuy(Hero hero, Market market) {
        List<Item> stock = market.getMarketItems().getInventory();
        if (stock.isEmpty()) {
            System.out.println("The market is sold out.");
            return;
        }

        Item item = selectItem("Items for sale:", stock);
        if (item != null) {
            System.out.println(market.buyItem(hero, item).getMessage());
        }
    }

    // gets all items a hero has, and prompts them to select the item they want to sell
    private void handleSell(Hero hero, Market market) {
        List<Item> owned = hero.getBackpack().getItemsOfType(Item.class);
        if (owned.isEmpty()) {
            System.out.println(hero.getName() + " has nothing to sell.");
            return;
        }

        Item item = selectItem("Choose an item to sell:", owned,
                i -> i + " -> sells for " + (int) i.getSellPrice() + " gold");
        if (item != null) {
            System.out.println(market.sellItem(hero, item).getMessage());
        }
    }

    // 6.
    // End of Game

    // prints when heroes lose the game
    public void showGameOver(HeroGroup heroes) {
        System.out.println("\nAll heroes have fainted!");
        quitGameText(heroes);
    }

    // text displayed when user decides to quit, just displaying some stats achieved
    public void quitGameText(HeroGroup heroes) {
        System.out.println("\n========== GAME OVER ==========");
        System.out.println("Highest Level Achieved: " + heroes.getHighestLevelHero());
        System.out.println("Highest XP Achieved: " + heroes.getHighestXP());
        System.out.println("Highest Gold Achieved: " + heroes.getHighestGold());
        System.out.println("Highest Mana Achieved: " + heroes.getHighestMana());
        System.out.println("================================");
    }

    // reusable (Y/N) prompt
    public boolean askYesNo(String question) {
        System.out.print(question + " (Y/N): ");
        return readChoice("YN", "Enter Y or N:") == 'Y';
    }

    // 7.
    // Item Selection Helpers

    // reusable prompt to display a list of items for a user to pick from
    // can be any generic type
    // label a function that converts each item into a String for display
    public <T> T selectItem(String prompt, List<T> items, Function<T, String> label) {
        // Display the message explaining what the user is choosing
        System.out.println(prompt);

        // Display each item with a number next to it
        // The label function determines how each item is shown to the user
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + label.apply(items.get(i)));
        }

        // Give the user the option to cancel without selecting anything.
        System.out.println("0. Cancel");

        // Read the user's choice and make sure it is within the available range.
        int choice = readInt(0, items.size());

        // Return null if the user cancels if they dont cancel return the selected item.
        return choice == 0 ? null : items.get(choice - 1);
    }

    // reuses the prior method above so we dont need to repeat the logic
    // String::valueOf converts each item into a String for display.
    // Used when we dont care about customizing how an item is display just uses default
    public <T> T selectItem(String prompt, List<T> items) {
        return selectItem(prompt, items, String::valueOf);
    }

    // main prompt for inventory actions
    public char getInventoryChoice(Hero hero) {
        System.out.println("\n========== " + hero.getName().toUpperCase() + "'S INVENTORY ==========");
        System.out.println("  W - Equip a weapon");
        System.out.println("  E - Equip armor");
        System.out.println("  P - Use a potion");
        System.out.println("  U - Unequip weapon");
        System.out.println("  R - Remove armor");
        System.out.println("  V - View backpack");
        System.out.println("  B - Back");

        return readChoice("WEPURVB", "Enter W, E, P, U, R, V, or B:");
    }
}
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;

public class Controller {
    private final Scanner scanner = new Scanner(System.in);

    // ------------------------------------------------------------------
    // Input helpers: every read goes through nextLine(), so no newline is
    // ever left behind in the buffer and empty input can't crash charAt(0).
    // ------------------------------------------------------------------

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

    // returns an int in [min, max], re-prompting on bad input
    private int readInt(int min, int max) {
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // not a number, fall through to the re-prompt
            }
            System.out.print("Enter a number between " + min + " and " + max + ": ");
        }
    }

    // ------------------------------------------------------------------
    // Welcome / setup
    // ------------------------------------------------------------------

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
        System.out.println("purchase powerful items, and grow stronger.");
        System.out.println();
        System.out.println("HOW TO PLAY:");
        System.out.println("  W - Move up");
        System.out.println("  A - Move left");
        System.out.println("  S - Move down");
        System.out.println("  D - Move right");
        System.out.println("  I - View hero information");
        System.out.println("  M - Enter a market (when on a market space)");
        System.out.println("  Q - Quit the game");
        System.out.println();
        System.out.println("THE WORLD:");
        System.out.println("  [ ] Common space: You may encounter monsters.");
        System.out.println("  [M] Market: Buy and sell items.");
        System.out.println("  [X] Inaccessible: You cannot enter these spaces.");
        System.out.println("  [P] Your party's current location.");
        System.out.println();
        System.out.println("BATTLES:");
        System.out.println("  Your heroes fight monsters in turn-based battles.");
        System.out.println("  Attack, cast spells, use potions, and equip gear");
        System.out.println("  to defeat your enemies.");
        System.out.println("  Victories reward gold and experience.");
        System.out.println("  Be careful! If all your heroes faint, the game ends.");
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

    // ------------------------------------------------------------------
    // Exploration
    // ------------------------------------------------------------------

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

    // ------------------------------------------------------------------
    // Hero selection
    // ------------------------------------------------------------------

    private String[] displayHeroOptions(String filePath, String heroType, Set<String> selectedNames) {
        List<String[]> availableHeroes = createAvailableHeroes(filePath, selectedNames);

        if (availableHeroes.isEmpty()) {
            System.out.println("No more available " + heroType + "s.");
            return null;
        }

        System.out.println("\n========== " + heroType.toUpperCase() + " OPTIONS ==========");

        for (int i = 0; i < availableHeroes.size(); i++) {
            String[] attributes = availableHeroes.get(i);
            String name = attributes[0].replace("_", " ");

            System.out.println((i + 1) + ". " + name);
            System.out.println(
                    "   Mana: " + attributes[1]
                            + " | Strength: " + attributes[2]
                            + " | Agility: " + attributes[3]
                            + " | Dexterity: " + attributes[4]
                            + " | Starting Gold: " + attributes[5]
                            + " | Starting XP: " + attributes[6]
            );
            System.out.println();
        }

        System.out.print("Enter your choice (1 to " + availableHeroes.size() + "): ");
        int heroChoice = readInt(1, availableHeroes.size());

        return availableHeroes.get(heroChoice - 1);
    }

    private static List<String[]> createAvailableHeroes(String filePath, Set<String> selectedNames) {
        List<String[]> availableHeroes = new ArrayList<>();

        try (Scanner fileScanner = new Scanner(new File(filePath))) {
            // Skip the header.
            if (fileScanner.hasNextLine()) {
                fileScanner.nextLine();
            }

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] attributes = line.split("\\s+");
                String name = attributes[0].replace("_", " ");

                // Skip heroes that have already been selected.
                if (selectedNames.contains(name)) {
                    continue;
                }

                availableHeroes.add(attributes);
            }
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Could not find " + filePath, e);
        }
        return availableHeroes;
    }

    public Sorcerer displaySorcererOptions(Set<String> selectedNames) {
        String[] attributes = displayHeroOptions("src/data/Sorcerers.txt", "Sorcerer", selectedNames);

        if (attributes == null) {
            return null;
        }

        return new Sorcerer(
                attributes[0].replace("_", " "),
                Integer.parseInt(attributes[1]),
                Integer.parseInt(attributes[2]),
                Integer.parseInt(attributes[3]),
                Integer.parseInt(attributes[4]),
                Integer.parseInt(attributes[5]),
                Integer.parseInt(attributes[6])
        );
    }

    public Paladin displayPaladinOptions(Set<String> selectedNames) {
        String[] attributes = displayHeroOptions("src/data/Paladins.txt", "Paladin", selectedNames);

        if (attributes == null) {
            return null;
        }

        return new Paladin(
                attributes[0].replace("_", " "),
                Integer.parseInt(attributes[1]),
                Integer.parseInt(attributes[2]),
                Integer.parseInt(attributes[3]),
                Integer.parseInt(attributes[4]),
                Integer.parseInt(attributes[5]),
                Integer.parseInt(attributes[6])
        );
    }

    public Warrior displayWarriorOptions(Set<String> selectedNames) {
        String[] attributes = displayHeroOptions("src/data/Warriors.txt", "Warrior", selectedNames);

        if (attributes == null) {
            return null;
        }

        return new Warrior(
                attributes[0].replace("_", " "),
                Integer.parseInt(attributes[1]),
                Integer.parseInt(attributes[2]),
                Integer.parseInt(attributes[3]),
                Integer.parseInt(attributes[4]),
                Integer.parseInt(attributes[5]),
                Integer.parseInt(attributes[6])
        );
    }

    public List<Hero> selectHero(int heroCount) {
        List<Hero> selectedHeroes = new ArrayList<>();
        Set<String> selectedNames = new HashSet<>();

        for (int i = 0; i < heroCount; i++) {
            System.out.println("\nSelect a hero type:");
            System.out.println("W - Warrior");
            System.out.println("S - Sorcerer");
            System.out.println("P - Paladin");

            char heroChoice = readChoice("WSP", "Invalid choice. Enter W, S, or P:");

            Hero selectedHero = switch (heroChoice) {
                case 'W' -> displayWarriorOptions(selectedNames);
                case 'S' -> displaySorcererOptions(selectedNames);
                case 'P' -> displayPaladinOptions(selectedNames);
                default -> null;
            };

            if (selectedHero != null) {
                selectedHeroes.add(selectedHero);
                selectedNames.add(selectedHero.getName());
            } else {
                // Let the user retry this hero selection.
                i--;
            }
        }

        return selectedHeroes;
    }

    // ------------------------------------------------------------------
    // Battle
    // ------------------------------------------------------------------

    // returns the zero-based index of the chosen hero, or -1 if the party is empty
    public int selectHero(HeroGroup party) {
        if (party.getNumHeroes() < 1) {
            return -1;
        }

        party.displayHeroGroupInformation();
        System.out.println("Select a hero you'd like to use: (1 to " + party.getNumHeroes() + "):");
        return readInt(1, party.getNumHeroes()) - 1;
    }

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

    // ------------------------------------------------------------------
    // Market / game over
    // ------------------------------------------------------------------

    // Only displays the stock for now; buying, selling, and reading the
    // player's choice still need to be added.
    public void displayMarketMenu(Hero hero, Market market) {
        System.out.println("\nWelcome to the market " + hero.getName() + "!");

        boolean shopping = true;
        while (shopping) {
            System.out.println("\n" + hero.getName() + " | Level " + hero.getLevel()
                    + " | Gold: " + (int) hero.getMoney());
            System.out.println("  B - Buy   S - Sell   L - Leave");

            System.out.println("  B - Buy   S - Sell   R - Repair   L - Leave");

            switch (readChoice("BSRL", "Enter B, S, R, or L:")) {
                case 'B': handleBuy(hero, market); break;
                case 'S': handleSell(hero, market); break;
                case 'R': handleRepair(hero, market); break;
                case 'L': shopping = false; break;
            }
        }
    }

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

    public void showGameOver(HeroGroup heroes) {
        System.out.println("\nAll heroes have fainted!");
        quitGame(heroes);
    }

    public void quitGame(HeroGroup heroes) {
        System.out.println("\n========== GAME OVER ==========");
        System.out.println("Highest Level Achieved: " + heroes.getHighestLevelHero());
        System.out.println("Highest XP Achieved: " + heroes.getHighestXP());
        System.out.println("Highest Gold Achieved: " + heroes.getHighestGold());
        System.out.println("Highest Mana Achieved: " + heroes.getHighestMana());
        System.out.println("================================");
    }

    public boolean askYesNo(String question) {
        System.out.print(question + " (Y/N): ");
        return readChoice("YN", "Enter Y or N:") == 'Y';
    }

    public <T> T selectItem(String prompt, List<T> items, Function<T, String> label) {
        System.out.println(prompt);
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + label.apply(items.get(i)));
        }
        System.out.println("0. Cancel");

        int choice = readInt(0, items.size());
        return choice == 0 ? null : items.get(choice - 1);
    }

    public <T> T selectItem(String prompt, List<T> items) {
        return selectItem(prompt, items, String::valueOf);
    }

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
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Factory Class for Generating Items
 * Abstracts the logic for generating an item so a market can easily request a random supply of items
 * Loads items into seperate arrays based on their type
 */

public class ItemFactory {
    private final List<Weapon> weapons;
    private final List<Armor> armory;
    private final List<Potion> potions;
    private final List<Spell> fireSpells;
    private final List<Spell> iceSpells;
    private final List<Spell> lightningSpells;

    public ItemFactory() {
        this.weapons = loadWeapons("src/data/Weaponry.txt");
        this.armory = loadArmory("src/data/Armory.txt");
        this.potions = loadPotions("src/data/Potions.txt");
        this.fireSpells = loadSpells("src/data/FireSpells.txt", SpellType.FIRE);
        this.iceSpells = loadSpells("src/data/IceSpells.txt", SpellType.ICE);
        this.lightningSpells = loadSpells(
                "src/data/LightningSpells.txt",
                SpellType.LIGHTNING
        );
    }

    public List<Weapon> getWeapons() {
        return Collections.unmodifiableList(weapons);
    }

    public List<Armor> getArmory() {
        return Collections.unmodifiableList(armory);
    }

    public List<Potion> getPotions() {
        return Collections.unmodifiableList(potions);
    }

    public List<Spell> getFireSpells() {
        return Collections.unmodifiableList(fireSpells);
    }

    public List<Spell> getIceSpells() {
        return Collections.unmodifiableList(iceSpells);
    }

    public List<Spell> getLightningSpells() {
        return Collections.unmodifiableList(lightningSpells);
    }

    private List<String[]> loadItemData(String filePath) {
        List<String[]> items = new ArrayList<>();

        try (Scanner fileScanner = new Scanner(new File(filePath))) {
            if (fileScanner.hasNextLine()) {
                fileScanner.nextLine(); // Skip header
            }

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim(); // remove trailing spaces

                if (line.isEmpty()) { // skip empty lines
                    continue;
                }

                items.add(line.split("\\s+")); // split by the spaces in between to delimit the items and add into the list
            }

        } catch (FileNotFoundException e) {
            throw new IllegalStateException(
                    "Could not find " + filePath, e
            );
        }

        return items;
    }

    private List<Weapon> loadWeapons(String filePath) {
        List<Weapon> weapons = new ArrayList<>();

        for (String[] attributes : loadItemData(filePath)) { // load item data stores attributes in a list
            String name = attributes[0]; // for each type of item the number of attributes and required data type varies
            double price = Double.parseDouble(attributes[1]);
            int level = Integer.parseInt(attributes[2]);
            int damage = Integer.parseInt(attributes[3]);
            int handsRequired = Integer.parseInt(attributes[4]);

            weapons.add(new Weapon(
                    name, price, level, damage, handsRequired
            ));
        }

        return weapons;
    }

    private List<Armor> loadArmory(String filePath) {
        List<Armor> armory = new ArrayList<>();

        for (String[] attributes : loadItemData(filePath)) {
            String name = attributes[0];
            double price = Double.parseDouble(attributes[1]);
            int level = Integer.parseInt(attributes[2]);
            int damageReduction = Integer.parseInt(attributes[3]);

            armory.add(new Armor(
                    name, price, level, damageReduction
            ));
        }

        return armory;
    }

    private List<Potion> loadPotions(String filePath) {
        List<Potion> potions = new ArrayList<>();

        for (String[] attributes : loadItemData(filePath)) {
            String name = cleanName(attributes[0]);
            double price = Double.parseDouble(attributes[1]);
            int level = Integer.parseInt(attributes[2]);
            int effectAmount = Integer.parseInt(attributes[3]);
            Set<HeroAttribute> affected = parseAttributes(attributes[4]);

            potions.add(new Potion(
                    name, price, level, effectAmount, affected
            ));
        }

        return potions;
    }


    // helper method to parse effected attributes for spells which are delimited by '/'
    private Set<HeroAttribute> parseAttributes(String column) {
        // All of the elements in an enum set must come from a single enum type that is specified
        Set<HeroAttribute> attributes = EnumSet.noneOf(HeroAttribute.class);

        for (String attribute : column.split("/")) {
            attributes.add(
                    HeroAttribute.valueOf(attribute.trim().toUpperCase())
            );
        }

        return attributes;
    }

    private List<Spell> loadSpells(String filePath, SpellType type) {
        List<Spell> spells = new ArrayList<>();

        for (String[] attributes : loadItemData(filePath)) {
            String name = attributes[0];
            double price = Double.parseDouble(attributes[1]);
            int level = Integer.parseInt(attributes[2]);
            int damage = Integer.parseInt(attributes[3]);
            int manaCost = Integer.parseInt(attributes[4]);

            spells.add(new Spell(
                    name, price, level, damage, manaCost, type
            ));
        }

        return spells;
    }

    // helper method to remove the underscore in between names
    private String cleanName(String raw) {
        return raw.replace('_', ' ');
    }

    // combines all items into one big list
    public List<Item> getAllItems() {
        List<Item> allItems = new ArrayList<>();

        allItems.addAll(weapons);
        allItems.addAll(armory);
        allItems.addAll(potions);
        allItems.addAll(fireSpells);
        allItems.addAll(iceSpells);
        allItems.addAll(lightningSpells);

        return allItems;
    }
}
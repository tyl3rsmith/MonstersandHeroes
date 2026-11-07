import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

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
        this.lightningSpells = loadSpells("src/data/LightningSpells.txt", SpellType.LIGHTNING);
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public List<Armor> getArmory() {
        return armory;
    }

    public List<Potion> getPotions() {
        return potions;
    }

    public List<Spell> getFireSpells() {
        return fireSpells;
    }

    public List<Spell> getIceSpells() {
        return iceSpells;
    }

    public List<Spell> getLightningSpells() {
        return lightningSpells;
    }

    private List<String[]> loadItemData(String filePath) {
        List<String[]> items = new ArrayList<>();

        try (Scanner fileScanner = new Scanner(new File(filePath))) {
            fileScanner.nextLine(); // skip header

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] attributes = line.split("\\s+");
                items.add(attributes);
            }

        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Could not find " + filePath, e);
        }

        return items;
    }

    private List<Weapon> loadWeapons(String filePath) {
        List<Weapon> weapons = new ArrayList<>();

        for (String[] attributes : loadItemData(filePath)) {
            String name = attributes[0];
            double price = Double.parseDouble(attributes[1]);
            int level = Integer.parseInt(attributes[2]);
            int damage = Integer.parseInt(attributes[3]);
            int handsRequired = Integer.parseInt(attributes[4]);

            weapons.add(new Weapon(name, price, level, damage, handsRequired));
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

            armory.add(new Armor(name, price, level, damageReduction));
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

            potions.add(new Potion(name, price, level, effectAmount, affected));
        }
        return potions;
    }

    private Set<HeroAttribute> parseAttributes(String column) {
        Set<HeroAttribute> result = EnumSet.noneOf(HeroAttribute.class);
        for (String s : column.split("/")) {
            result.add(HeroAttribute.valueOf(s.trim().toUpperCase()));
        }
        return result;
    }

    private List<Spell> loadSpells(String filePath, SpellType type) {
        List<Spell> spells = new ArrayList<>();

        for (String[] attributes : loadItemData(filePath)) {
            String name = attributes[0];
            double price = Double.parseDouble(attributes[1]);
            int level = Integer.parseInt(attributes[2]);
            int damage = Integer.parseInt(attributes[3]);
            int manaCost = Integer.parseInt(attributes[4]);

            spells.add(new Spell(name, price, level, damage, manaCost, type));
        }
        return spells;
    }

    private String cleanName(String raw) {
        return raw.replace('_', ' ');
    }

    public List<Item> getAllItems() {
        List<Item> all = new ArrayList<>();
        all.addAll(weapons);
        all.addAll(armory);
        all.addAll(potions);
        all.addAll(fireSpells);
        all.addAll(iceSpells);
        all.addAll(lightningSpells);
        return all;
    }
}

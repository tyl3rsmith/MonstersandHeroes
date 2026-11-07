import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Factory Class for Generating Heroes
 * Abstracts the logic for generating a hero so during the start of the game a user is able to just select a hero they want
 */

public class HeroFactory {
    private final Map<HeroType, List<HeroTemplate>> templates = new EnumMap<>(HeroType.class);

    public HeroFactory() {
        for (HeroType type : HeroType.values()) {
            templates.put(type, loadTemplates(fileFor(type)));
        }
    }

    // fresh heroes of the given type, skipping any whose name is already taken
    public List<Hero> getAvailableHeroes(HeroType type, Set<String> excludedNames) {
        List<Hero> available = new ArrayList<>();
        for (HeroTemplate template : templates.get(type)) {
            if (!excludedNames.contains(template.name)) {
                available.add(template.create(type));
            }
        }
        return available;
    }

    private static String fileFor(HeroType type) {
        switch (type) {
            case WARRIOR:  return "src/data/Warriors.txt";
            case SORCERER: return "src/data/Sorcerers.txt";
            case PALADIN:  return "src/data/Paladins.txt";
            default:
                throw new IllegalArgumentException("No data file for hero type " + type);
        }
    }

    // file columns: name, mana, strength, agility, dexterity, starting gold, starting xp
    private List<HeroTemplate> loadTemplates(String filePath) {
        List<HeroTemplate> result = new ArrayList<>();

        try (Scanner fileScanner = new Scanner(new File(filePath))) {
            if (fileScanner.hasNextLine()) {
                fileScanner.nextLine(); // skip header
            }

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] a = line.split("\\s+");
                result.add(new HeroTemplate(
                        a[0].replace('_', ' '),
                        Integer.parseInt(a[1]),
                        Integer.parseInt(a[2]),
                        Integer.parseInt(a[3]),
                        Integer.parseInt(a[4]),
                        Integer.parseInt(a[5]),
                        Integer.parseInt(a[6])));
            }
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Could not find " + filePath, e);
        }

        return result;
    }

    // the parsed default values for one hero; never handed out, only used to build Heroes
    private static class HeroTemplate {
        private final String name;
        private final int mana;
        private final int strength;
        private final int agility;
        private final int dexterity;
        private final int gold;
        private final int xp;

        private HeroTemplate(String name, int mana, int strength, int agility,
                             int dexterity, int gold, int xp) {
            this.name = name;
            this.mana = mana;
            this.strength = strength;
            this.agility = agility;
            this.dexterity = dexterity;
            this.gold = gold;
            this.xp = xp;
        }

        private Hero create(HeroType type) {
            switch (type) {
                case WARRIOR:  return new Warrior(name, mana, strength, agility, dexterity, gold, xp);
                case SORCERER: return new Sorcerer(name, mana, strength, agility, dexterity, gold, xp);
                case PALADIN:  return new Paladin(name, mana, strength, agility, dexterity, gold, xp);
                default:
                    throw new IllegalArgumentException("Unknown hero type " + type);
            }
        }
    }
}
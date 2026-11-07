import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Factory Class for Generating Monsters
 * Abstracts the logic for generating a monster so during the start of the battle, monsters are generated without needing to know how
 */

public class MonsterFactory {
    private final List<Dragon> dragons;
    private final List<Exoskeleton> exoskeletons;
    private final List<Spirit> spirits;

    private final Random rand = new Random();

    public MonsterFactory() {
        this.dragons = loadDragons("src/data/Dragons.txt");
        this.exoskeletons = loadExoskeletons("src/data/Exoskeletons.txt");
        this.spirits = loadSpirits("src/data/Spirits.txt");
    }

    // during the battle monsters of any type are created at random w.r.t. highest hero level
    public Monster createRandomMonster(MonsterType type, int level) {
        if (type == null) {
            throw new IllegalArgumentException("Monster type cannot be null.");
        }

        if (level < 1) {
            throw new IllegalArgumentException("Level must be positive.");
        }

        return switch (type) {
            case DRAGON -> createDragon(level);
            case EXOSKELETON -> createExoskeleton(level);
            case SPIRIT -> createSpirit(level);
        };
    }

    // selects a random monster based on any subtype of monster
    private Monster selectRandomMonster(List<? extends Monster> monsters, int level) {
        if (monsters.isEmpty()) {
            throw new IllegalStateException("No monsters were loaded.");
        }

        // Find all monsters closest to the requested level incase there are none of the exact level that match
        int bestGap = Integer.MAX_VALUE;
        List<Monster> closest = new ArrayList<>();

        for (Monster monster : monsters) {
            int gap = Math.abs(monster.getLevel() - level);

            if (gap < bestGap) {
                bestGap = gap;
                closest.clear();
                closest.add(monster);
            } else if (gap == bestGap) {
                closest.add(monster);
            }
        }

        return closest.get(rand.nextInt(closest.size()));
    }

    private Dragon createDragon(int level) {
        Dragon selected = (Dragon) selectRandomMonster(dragons, level);

        return new Dragon(
                selected.getName(),
                selected.getLevel(),
                selected.getHp(),
                selected.getBaseDamage(),
                selected.getDefense(),
                selected.getDodgeChance()
        );
    }

    private Exoskeleton createExoskeleton(int level) {
        Exoskeleton selected = (Exoskeleton) selectRandomMonster(exoskeletons, level);

        return new Exoskeleton(
                selected.getName(),
                selected.getLevel(),
                selected.getHp(),
                selected.getBaseDamage(),
                selected.getDefense(),
                selected.getDodgeChance()
        );
    }

    private Spirit createSpirit(int level) {
        Spirit selected = (Spirit) selectRandomMonster(spirits, level);

        return new Spirit(
                selected.getName(),
                selected.getLevel(),
                selected.getHp(),
                selected.getBaseDamage(),
                selected.getDefense(),
                selected.getDodgeChance()
        );
    }

    // Monster HP = 100 * level
    private int calculateMonsterHP(int level) {
        return 100 * level;
    }

    private List<String[]> loadMonsterData(String filePath) {
        List<String[]> monsters = new ArrayList<>();

        try (Scanner fileScanner = new Scanner(new File(filePath))) {
            if (fileScanner.hasNextLine()) {
                fileScanner.nextLine(); // Skip header
            }

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] attributes = line.split("\\s+");

                if (attributes.length != 5) {
                    throw new IllegalStateException(
                            "Invalid monster data in " + filePath + ": " + line
                    );
                }

                monsters.add(attributes);
            }

        } catch (FileNotFoundException e) {
            throw new IllegalStateException(
                    "Could not find " + filePath, e
            );
        }

        return monsters;
    }

    private List<Dragon> loadDragons(String filePath) {
        List<Dragon> dragons = new ArrayList<>();

        for (String[] attributes : loadMonsterData(filePath)) {
            String name = attributes[0];
            int level = Integer.parseInt(attributes[1]);
            int damage = Integer.parseInt(attributes[2]);
            int defense = Integer.parseInt(attributes[3]);
            int dodgeChance = Integer.parseInt(attributes[4]);

            dragons.add(new Dragon(
                    name,
                    level,
                    calculateMonsterHP(level),
                    damage,
                    defense,
                    dodgeChance
            ));
        }

        return dragons;
    }

    private List<Exoskeleton> loadExoskeletons(String filePath) {
        List<Exoskeleton> exoskeletons = new ArrayList<>();

        for (String[] attributes : loadMonsterData(filePath)) {
            String name = attributes[0];
            int level = Integer.parseInt(attributes[1]);
            int damage = Integer.parseInt(attributes[2]);
            int defense = Integer.parseInt(attributes[3]);
            int dodgeChance = Integer.parseInt(attributes[4]);

            exoskeletons.add(new Exoskeleton(
                    name,
                    level,
                    calculateMonsterHP(level),
                    damage,
                    defense,
                    dodgeChance
            ));
        }

        return exoskeletons;
    }

    private List<Spirit> loadSpirits(String filePath) {
        List<Spirit> spirits = new ArrayList<>();

        for (String[] attributes : loadMonsterData(filePath)) {
            String name = attributes[0];
            int level = Integer.parseInt(attributes[1]);
            int damage = Integer.parseInt(attributes[2]);
            int defense = Integer.parseInt(attributes[3]);
            int dodgeChance = Integer.parseInt(attributes[4]);

            spirits.add(new Spirit(
                    name,
                    level,
                    calculateMonsterHP(level),
                    damage,
                    defense,
                    dodgeChance
            ));
        }

        return spirits;
    }
}
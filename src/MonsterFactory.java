import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

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

    public Monster createRandomMonster(MonsterType type, int level) {
        if (level < 1) {
            throw new IllegalArgumentException("Level must be greater than or equal to 1");
        }
        switch (type) {
            case DRAGON:
                return createDragon(level);
            case EXOSKELETON:
                return createExoskeleton(level);
            case SPIRIT:
                return createSpirit(level);
            default:
                throw new IllegalArgumentException("Invalid monster type.");
        }
    }

    private Monster selectRandomMonster(List<? extends Monster> monsters, int level) {
        if (monsters.isEmpty()) {
            throw new IllegalStateException("No monsters were loaded");
        }

        // keep every monster tied for the closest level to the requested one
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
        return new Dragon(selected.getName(), selected.getLevel(), selected.getHp(), selected.getBaseDamage(), selected.getDefense(), selected.getDodgeChance());
    }

    private Exoskeleton createExoskeleton(int level) {
        Exoskeleton selected = (Exoskeleton) selectRandomMonster(exoskeletons, level);
        return new Exoskeleton(selected.getName(), selected.getLevel(), selected.getHp(), selected.getBaseDamage(), selected.getDefense(), selected.getDodgeChance());
    }

    private Spirit createSpirit(int level) {
        Spirit selected = (Spirit) selectRandomMonster(spirits, level);
        return new Spirit(selected.getName(), selected.getLevel(), selected.getHp(), selected.getBaseDamage(), selected.getDefense(), selected.getDodgeChance());
    }

    // monster hp = 100 * level
    private int calculateMonsterHP(int level) {
        if (level < 1) {
            throw new IllegalArgumentException("Level must be positive.");
        }
        return 100 * level;
    }

    private List<String[]> loadMonsterData(String filePath) {
        List<String[]> monsters = new ArrayList<>();

        try (Scanner fileScanner = new Scanner(new File(filePath))) {
            if (fileScanner.hasNextLine()) {
                fileScanner.nextLine(); // skip header
            }

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] attributes = line.split("\\s+");
                monsters.add(attributes);
            }

        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Could not find " + filePath, e);
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

            dragons.add(new Dragon(name, level, calculateMonsterHP(level), damage, defense, dodgeChance));
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

            exoskeletons.add(new Exoskeleton(name, level, calculateMonsterHP(level), damage, defense, dodgeChance));
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

            spirits.add(new Spirit(name, level, calculateMonsterHP(level), damage, defense, dodgeChance));
        }

        return spirits;
    }
}
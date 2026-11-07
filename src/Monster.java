import java.util.Random;

/**
 * Monster (Abstract)
 * Manages the shared state and behaviors of all monsters
 * The 3 types (Spirit, Exoskeleton, and Dragon) extend Monster and are the concrete types
 */

public abstract class Monster {
    private final String name;
    private final int level;
    private int hp;
    private int baseDamage;
    private int defense;
    private double dodgeChance;

    private static final Random rand = new Random();

    public Monster(String name, int level, int hp, int baseDamage,
                   int defense, double dodgeChance) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Monster must have a name.");
        }

        if (level < 1 || hp < 0 || baseDamage < 0
                || defense < 0 || dodgeChance < 0 || dodgeChance > 100) {
            throw new IllegalArgumentException("Invalid monster attributes.");
        }

        this.name = name;
        this.level = level;
        this.hp = hp;
        this.baseDamage = baseDamage;
        this.defense = defense;
        this.dodgeChance = dodgeChance * 0.01;
    }

    // basic getters

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getHp() {
        return hp;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public int getDefense() {
        return defense;
    }

    public double getDodgeChance() {
        return dodgeChance;
    }

    public static MonsterType getRandomMonsterType() {
        // Select a monster type with equal probability
        MonsterType[] types = MonsterType.values();

        return types[rand.nextInt(types.length)];
    }

    public int takeDamage(int rawDamage) {
        // Defense reduces incoming damage
        double multiplier = 100.0 / (100.0 + defense);

        // Damage is always at least 1
        int actualDamage = Math.max(1, (int) (rawDamage * multiplier));

        hp = Math.max(0, hp - actualDamage);

        return actualDamage;
    }

    // used by spells to affect attributes of the monsters
    public void reduceBaseDamage(double fraction) {
        baseDamage = (int) (baseDamage * (1 - fraction));
    }

    public void reduceDefense(double fraction) {
        defense = (int) (defense * (1 - fraction));
    }

    public void reduceDodgeChance(double fraction) {
        dodgeChance *= (1 - fraction);
    }
}
import java.util.Random;

public abstract class Monster {
    private final String name;
    private final int level;
    private int hp;
    private int baseDamage;
    private int defense;
    private double dodgeChance;

    public Monster(String name, int level, int hp, int baseDamage, int defense, double dodgeChance) {
        if (name == null) {
            throw new IllegalArgumentException("hero must have a name");
        }
        if (level < 0 || hp < 0 || baseDamage < 0 || defense < 0 || dodgeChance < 0) {
            throw new IllegalArgumentException("values cannot be negative");
        }

        this.name = name;
        this.level = level;
        this.hp = hp;
        this.baseDamage = baseDamage;
        this.defense = defense;
        this.dodgeChance = dodgeChance * 0.01;
    }

    public int getHp() {
        return hp;
    }

    public int getLevel() {
        return level;
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

    public String getName() {
        return name;
    }

    public static MonsterType getRandomMonsterType() {
        // select a monster type with equal probability
        Random rand = new Random();
        MonsterType[] types = MonsterType.values();

        return types[rand.nextInt(types.length)];
    }

    public int takeDamage(int rawDamage) {
        // defense 200 -> damage multiplied by 100/300, about a third
        double multiplier = 100.0 / (100.0 + defense);
        int actual = Math.max(1, (int) (rawDamage * multiplier)); // always at least 1
        hp = Math.max(0, hp - actual);
        return actual;
    }

    public void reduceBaseDamage(double fraction) { baseDamage = (int) (baseDamage * (1 - fraction)); }
    public void reduceDefense(double fraction)    { defense = (int) (defense * (1 - fraction)); }
    public void reduceDodgeChance(double fraction) { dodgeChance = (int) (dodgeChance * (1 - fraction)); }
}

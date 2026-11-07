/**
 * Used to define the type Warrior
 * All heroes essentially reuse Parent logic
 * The data already provides the functionality to account for favored attributes
 * This is just useful for future extensions of Warrior
 */

public class Warrior extends Hero {
    public static final HeroType heroType = HeroType.WARRIOR;

    public Warrior(String name, int mana, int strength, int agility, int dexterity, int money, int xp) {
        super(name, mana, strength, agility, dexterity, money, xp);
    }

    public HeroType getHeroType() {
        return heroType;
    }
}
/**
 * Used to define the type Sorcerer
 * All heroes essentially reuse Parent logic
 * The data already provides the functionality to account for favored attributes
 * This is just useful for future extensions of Sorcerer
 */

public class Sorcerer extends Hero {
    public static final HeroType heroType = HeroType.SORCERER;

    public Sorcerer(String name, int mana, int strength, int agility, int dexterity, int money, int xp) {
        super(name, mana, strength, agility, dexterity, money, xp);
    }

    public HeroType getHeroType() {
        return heroType;
    }
}
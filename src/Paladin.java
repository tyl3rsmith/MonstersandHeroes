/**
 * Used to define the type Paladin
 * All heroes essentially reuse Parent logic
 * The data already provides the functionality to account for favored attributes
 * This is just useful for future extensions of Paladin
 */

public class Paladin extends Hero {
    private final HeroType heroType = HeroType.PALADIN;

    public Paladin(String name, int mana, int strength, int agility, int dexterity, int money, int xp) {
        super(name, mana, strength, agility, dexterity, money, xp);
    }

    public HeroType getHeroType() {
        return heroType;
    }
}
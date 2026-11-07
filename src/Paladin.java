public class Paladin extends Hero {
    private final HeroType heroType = HeroType.PALADIN;

    public Paladin(String name, int mana, int strength, int agility, int dexterity, int money, int xp) {
        super(name, mana, strength, agility, dexterity, money, xp);
    }

    public HeroType getHeroType() {
        return heroType;
    }
}

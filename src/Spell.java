public class Spell extends Item implements Castable {
    private int damage;
    private int manaCost;
    private SpellType type;

    public Spell(String name, double price, int level, int damage, int manaCost, SpellType type) {
        super(name, price, level);

        if (damage < 0) {
            throw new IllegalArgumentException("damage amount cannot be negative");
        }
        if (manaCost < 0) {
            throw new IllegalArgumentException("mana cost must be positive");
        }

        this.damage = damage;
        this.manaCost = manaCost;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("%s (Lv %d, %s, %d dmg, %d mana, %.0f gold)", getName(), getLevel(), type, damage, manaCost, getPrice());
    }

    @Override
    public void cast(Hero hero, Monster monster) {

    }

    public double getManaCost() {
        return manaCost;
    }


    public String applyEffect(Monster target) {
        switch (type) {
            case ICE:
                target.reduceBaseDamage(0.1);
                return target.getName() + "'s damage was reduced!";
            case FIRE:
                target.reduceDefense(0.1);
                return target.getName() + "'s defense was reduced!";
            case LIGHTNING:
                target.reduceDodgeChance(0.1);
                return target.getName() + " is slower to dodge!";
            default:
                return "";
        }
    }

    public double getDamage() {
        return damage;
    }

    @Override
    public Spell copy() {
        return new Spell(getName(), getPrice(), getLevel(), damage, manaCost, type);
    }
}

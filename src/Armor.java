/**
 * Represents a piece of armor that a hero can equip to reduce incoming damage.
 * Extends Gear, inheriting its name, price, level, and usage information.
 * Implements Equipable to allow heroes to equip and unequip armor.
 */

public class Armor extends Gear implements Equipable {
    private final int damageReduction;

    public Armor(String name, double price, int level, int damageReduction) {
        super(name, price, level);
        if (damageReduction < 0) {
            throw new IllegalArgumentException("damage reduction cannot be negative");
        }

        this.damageReduction = damageReduction;
    }

    @Override
    public String toString() {
        return String.format("%s (Lv %d, %d damage reduction, %s, %.0f gold)", getName(), getLevel(), damageReduction, describeUses(), getPrice());
    }

    @Override
    public Armor copy() { // independent copy with the same properties
        return new Armor(getName(), getPrice(), getLevel(), damageReduction);
    }

    @Override
    public void equip(Hero hero) { // delegate to hero
        hero.equipArmor(this);
    }

    @Override
    public void unequip(Hero hero) { // delegate to hero
        if (hero.getArmor() == this) {
            hero.unequipArmor();
        }
    }

    public int getDamageReduction() {
        return damageReduction;
    }
}
/**
 * Represents a weapon that a hero can equip to do more damage.
 * Extends Gear, inheriting its name, price, level, and usage information.
 * Implements Equipable to allow heroes to equip and unequip weapons.
 */

public class Weapon extends Gear implements Equipable {
    private final int damage;
    private final int handsRequired;

    public Weapon(String name, double price, int level, int damage, int handsRequired) {
        super(name, price, level);

        if (damage < 0) {
            throw new IllegalArgumentException("Weapon damage cannot be negative.");
        }

        if (handsRequired != 1 && handsRequired != 2) {
            throw new IllegalArgumentException("Weapon must require either 1 or 2 hands.");
        }

        this.damage = damage;
        this.handsRequired = handsRequired;
    }

    public int getWeaponDamage() {
        return damage;
    }

    public int getHandsRequired() {
        return handsRequired;
    }

    @Override
    public String toString() {
        return String.format(
                "%s (Lv %d, %d dmg, %d-handed, %s, %.0f gold)",
                getName(),
                getLevel(),
                damage,
                handsRequired,
                describeUses(),
                getPrice()
        );
    }

    @Override
    public Weapon copy() {
        return new Weapon(
                getName(),
                getPrice(),
                getLevel(),
                damage,
                handsRequired
        );
    }

    // delegates to hero tp equip weapon
    // both hands are required iff the weapon has handsRequired = 2
    @Override
    public void equip(Hero hero) {
        hero.equipWeapon(this, handsRequired == 2);
    }

    // delegates to hero to unequip weapon
    @Override
    public void unequip(Hero hero) {
        if (hero.getWeapon() == this) {
            hero.unequipWeapon();
        }
    }
}
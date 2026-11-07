public class Weapon extends Gear implements Buyable, Sellable, Equipable {
    private int damage;
    private final int handsRequired; // 1 or 2

    public Weapon(String name, double price, int level, int damage, int handsRequired) {
        super(name, price, level);

        if (damage < 0) {
            throw new IllegalArgumentException("weapon cannot do negative damage");
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
        return String.format("%s (Lv %d, %d dmg, %d-handed, %s, %.0f gold)", getName(), getLevel(), damage, handsRequired, describeUses(), getPrice());
    }


    @Override
    public Weapon copy() {
        return new Weapon(getName(), getPrice(), getLevel(), damage, handsRequired);
    }

    @Override
    public void equip(Hero hero) {

    }
}

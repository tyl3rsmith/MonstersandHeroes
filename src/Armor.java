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
    public Armor copy() {
        return new Armor(getName(), getPrice(), getLevel(), damageReduction);
    }

    @Override
    public void equip(Hero hero) {

    }

    public int getDamageReduction() {
        return damageReduction;
    }
}

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class Potion extends Item implements Useable {
    private final int effectAmount;
    private final Set<HeroAttribute> attributes;

    public Potion(String name, double price, int level, int effectAmount, Set<HeroAttribute> attributes) {
        super(name, price, level);
        if (effectAmount < 0) {
            throw new IllegalArgumentException("effect amount cannot be negative");
        }
        if (attributes == null || attributes.isEmpty()) {
            throw new IllegalArgumentException("potion must be applied to some attribute");
        }
        this.effectAmount = effectAmount;
        this.attributes = EnumSet.copyOf(attributes);
    }

    public int getEffectAmount() { return effectAmount; }

    public Set<HeroAttribute> getAttributes() {
        return Collections.unmodifiableSet(attributes);
    }

    private String describeAttributes() {
        StringBuilder sb = new StringBuilder();
        for (HeroAttribute attr : attributes) {
            if (!sb.isEmpty()) sb.append("/");
            String name = attr.name();
            sb.append(name.charAt(0)).append(name.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("%s (Lv %d, +%d %s, %.0f gold)", getName(), getLevel(), effectAmount, describeAttributes(), getPrice());
    }

    @Override
    public Potion copy() {
        return new Potion(getName(), getPrice(), getLevel(), effectAmount, attributes);
    }

    @Override
    public void use(Hero hero) {
        hero.applyPotion(this);
    }
}
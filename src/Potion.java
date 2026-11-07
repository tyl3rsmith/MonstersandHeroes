import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Potion
 * Subclass of Item and is Useable
 * Manages which attributes the potion effects and the effect amount
 * Delegates to hero to use the potion
 */


public class Potion extends Item implements Useable {
    private final int effectAmount;
    private final Set<HeroAttribute> attributes;

    public Potion(String name, double price, int level, int effectAmount, Set<HeroAttribute> attributes) {
        super(name, price, level);

        if (effectAmount < 0) {
            throw new IllegalArgumentException("Effect amount cannot be negative.");
        }

        if (attributes == null || attributes.isEmpty()) {
            throw new IllegalArgumentException("Potion must affect at least one attribute.");
        }

        this.effectAmount = effectAmount;
        this.attributes = EnumSet.copyOf(attributes);
    }

    public int getEffectAmount() {
        return effectAmount;
    }

    public Set<HeroAttribute> getAttributes() {
        return Collections.unmodifiableSet(attributes);
    }

    // helper method for displaying attributes
    private String describeAttributes() {
        StringBuilder description = new StringBuilder();

        for (HeroAttribute attribute : attributes) {
            if (!description.isEmpty()) {
                description.append("/");
            }

            String name = attribute.name();
            description.append(name.charAt(0)).append(name.substring(1).toLowerCase());
        }

        return description.toString();
    }

    // default display for a potion
    @Override
    public String toString() {
        return String.format(
                "%s (Lv %d, +%d %s, %.0f gold)",
                getName(),
                getLevel(),
                effectAmount,
                describeAttributes(),
                getPrice()
        );
    }

    @Override
    public Potion copy() {
        return new Potion(
                getName(),
                getPrice(),
                getLevel(),
                effectAmount,
                attributes
        );
    }

    // delegate to hero to apply potion to their attribute
    @Override
    public void use(Hero hero) {
        hero.applyPotion(this);
    }
}
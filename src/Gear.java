/**
 * Represents equipment that can be used by a hero, such as weapons and armor
 * Gear has a limited number of uses and can be repaired when it wears down
 */

public abstract class Gear extends Item implements Repairable {
    public static final int DEFAULT_MAX_USES = 10; // eventually could extend to allow items to define their own usage amount

    private final int maxUses;
    private int usesRemaining;

    protected Gear(String name, double price, int level) {
        this(name, price, level, DEFAULT_MAX_USES);
    }

    protected Gear(String name, double price, int level, int maxUses) {
        super(name, price, level);
        if (maxUses < 1) {
            throw new IllegalArgumentException("gear must have at least one use");
        }
        this.maxUses = maxUses;
        this.usesRemaining = maxUses;
    }

    public void use() { // each use reduces durability by 1, the battle will determine which uses actually count
        if (usesRemaining > 0) {
            usesRemaining--;
        }
    }

    public boolean isBroken() {
        return usesRemaining == 0;
    }

    public int getUsesRemaining() {
        return usesRemaining;
    }

    public int getMaxUses() {
        return maxUses;
    }

    @Override
    public boolean needsRepair() {
        return usesRemaining < maxUses;
    }

    @Override
    public double getRepairCost() {
        return getPrice() / 2; // repair costs half the purchase price
    }

    @Override
    public void repair() {
        usesRemaining = maxUses; // after repair the item is as good as new so reset uses
    }

    protected String describeUses() {
        return isBroken() ? "BROKEN" : usesRemaining + "/" + maxUses + " uses";
    }
}
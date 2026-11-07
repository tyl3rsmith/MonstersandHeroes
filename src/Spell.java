public class Spell extends Item implements Castable {
    private final int damage;
    private final int manaCost;
    private final SpellType type;

    public Spell(String name, double price, int level, int damage, int manaCost, SpellType type) {
        super(name, price, level);

        if (damage < 0) {
            throw new IllegalArgumentException("damage amount cannot be negative");
        }
        if (manaCost < 0) {
            throw new IllegalArgumentException("mana cost must be positive");
        }

        if (type == null) {
            throw new IllegalArgumentException("spell must have a type");
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
        int dealt = monster.takeDamage(hero.calculateSpellDamage(this));
        System.out.println(hero.getName() + " cast " + getName() + " on " + monster.getName() + " for " + dealt + " damage!");
        System.out.println(applyEffect(monster));
    }

    public int getManaCost() {
        return manaCost;
    }


    public String applyEffect(Monster target) {
        return switch (type) {
            case ICE -> {
                target.reduceBaseDamage(0.1);
                yield target.getName() + "'s damage was reduced!";
            }
            case FIRE -> {
                target.reduceDefense(0.1);
                yield target.getName() + "'s defense was reduced!";
            }
            case LIGHTNING -> {
                target.reduceDodgeChance(0.1);
                yield target.getName() + " is slower to dodge!";
            }
            default -> "";
        };
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public Spell copy() {
        return new Spell(getName(), getPrice(), getLevel(), damage, manaCost, type);
    }
}

import java.util.Random;

public abstract class Hero {
    private final String name;
    private double mana;
    private double strength;
    private double agility;
    private double dexterity;
    private double money;
    private int xp;
    private int level;
    private final Inventory backpack;
    private int hp;
    private boolean isFainted;
    private boolean usingBothHands;
    private Armor armor;
    static final Weapon FISTS = new Weapon("Fists", 0, 1, 75, 1);
    private Weapon weapon = FISTS;
    private final static Random rand = new Random();


    public Hero(String name, double mana, double strength, double agility, double dexterity, double money, int xp) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("hero must have a name");
        }
        if (mana < 0 || strength < 0 || agility < 0 || dexterity < 0 || money < 0 || xp < 0) {
            throw new IllegalArgumentException("values cannot be negative");
        }

        this.name = name;
        this.mana = mana;
        this.strength = strength;
        this.agility = agility;
        this.dexterity = dexterity;
        this.money = money;
        this.xp = xp;
        this.backpack = new Inventory();
        this.hp = 100;
        this.level = 1;
        this.isFainted = false;
        this.armor = null;
    }

    public boolean attemptToDodge() {
        double chance = Math.min(0.7, agility * 0.005); // max dodge chance is 70%
        return rand.nextDouble() < chance;
    }

    public void equipArmor(Armor newArmor) {
        if (newArmor == null) {
            throw new IllegalArgumentException("Please provide a non-null armor to be equipped");
        }
        if (newArmor.isBroken()) {
            throw new IllegalStateException(newArmor.getName() + " is broken and must be repaired first");
        }
        if (!backpack.contains(newArmor)) {
            throw new IllegalArgumentException("Hero does not own " + newArmor.getName());
        }
        backpack.remove(newArmor);
        if (armor != null) {
            backpack.add(armor); // return the old piece
        }
        armor = newArmor;
    }

    public void equipWeapon(Weapon newWeapon, boolean bothHands) {
        if (newWeapon == null) {
            throw new IllegalArgumentException("Please provide a non-null weapon to be equipped");
        }
        if (newWeapon.isBroken()) {
            throw new IllegalStateException(newWeapon.getName() + " is broken and must be repaired first");
        }
        if (!backpack.contains(newWeapon)) {
            throw new IllegalArgumentException("Hero does not own " + newWeapon.getName());
        }
        if (newWeapon.getHandsRequired() == 2 && !bothHands) {
            bothHands = true; // two-handed weapons always use both hands
        }

        unequipWeapon();

        backpack.remove(newWeapon);
        weapon = newWeapon;
        usingBothHands = bothHands;
    }

    public int calculateAttackDamage() {
        double effectiveStrength = usingBothHands && weapon.getHandsRequired() == 1 ? strength * 1.5 : strength;
        return (int) ((effectiveStrength + weapon.getWeaponDamage()) * 0.2); // changed to 0.2 cause 0.05 was too low
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setStrength(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("strength cannot be negative");
        }
        this.strength = amount;
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public void heal(double percentage) {
        if (percentage <= 0) {
            throw new IllegalArgumentException("Invalid percentage provided, must be > 0");
        }
        this.hp = (int) (this.hp * (1 + percentage));
    }


    public void setMana(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Mana cannot be negative");
        }
        this.mana = amount;
    }

    public void setHp(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Hp cannot be negative");
        }
        this.hp = amount;
    }

    public void setLevel(int amount) {
        if (amount < 1) {
            throw new IllegalArgumentException("Level cannot be less than 1");
        }
        this.level = amount;
    }

    public int takeDamage(int rawDamage) {
        int reduction = (armor == null) ? 0 : armor.getDamageReduction();
        int actual = Math.max(0, rawDamage - reduction);
        hp = Math.max(0, hp - actual);
        return actual;
    }

    public boolean getIsFainted() {
        return isFainted;
    }

    public void setIsFainted(boolean bool) {
        isFainted = bool;
    }

    public void gainExperience(int amount) {
        this.xp += amount;
    }

    public double getAgility() {
        return agility;
    }

    public double getDexterity() {
        return dexterity;
    }

    public double getMana() {
        return mana;
    }

    public double getMoney() {
        return money;
    }

    public double getStrength() {
        return strength;
    }

    public int getXp() {
        return xp;
    }

    public Inventory getBackpack() {
        return backpack;
    }

    public int getLevel() {
        return level;
    }

    public void addGold(double gold) {
        if (gold <= 0) {
            throw new IllegalArgumentException("You must specify a positive non-zero number of gold to give a hero");
        }
        this.money += gold;
    }


    public int xpNeededForNextLevel() {
        return level * (level + 1) * 5;   // 10 at level 1, 30 at level 2, 60 at level 3
    }

    public boolean canLevelUp() {
        return xp >= xpNeededForNextLevel();
    }

    public void levelUp() {
        level++;
        hp = level * 100;
        mana *= 1.1;
        strength *= (getHeroType() == HeroType.WARRIOR || getHeroType() == HeroType.PALADIN) ? 1.10 : 1.05;
        agility *= (getHeroType() == HeroType.WARRIOR || getHeroType() == HeroType.SORCERER) ? 1.10 : 1.05;
        dexterity *= (getHeroType() == HeroType.SORCERER || getHeroType() == HeroType.PALADIN) ? 1.10 : 1.05;
    }

    public abstract HeroType getHeroType();

    public void spendMana(double amount) {
        if (amount > mana) {
            throw new IllegalArgumentException("Not enough mana");
        }
        mana -= amount;
    }

    // spec: spell_damage + (dexterity / 10000) * spell_damage
    public int calculateSpellDamage(Spell spell) {
        return (int) (spell.getDamage() + (dexterity / 10000.0) * spell.getDamage());
    }

    public void applyPotion(Potion potion) {
        int amount = potion.getEffectAmount();
        for (HeroAttribute attr : potion.getAttributes()) {
            switch (attr) {
                case HEALTH:    hp += amount; break;
                case MANA:      mana += amount; break;
                case STRENGTH:  strength += amount; break;
                case DEXTERITY: dexterity += amount; break;
                case AGILITY:   agility += amount; break;
                case DEFENSE:   break; // heroes have no defense stat yet
            }
        }
    }

    public void spendGold(double amount) {
        if (amount < 0 || amount > money) {
            throw new IllegalArgumentException("Cannot spend " + amount + " gold");
        }
        money -= amount;
    }

    public Armor getArmor() {
        return armor;
    }

    public void unequipArmor() {
        if (armor != null) {
            backpack.add(armor);
            armor = null;
        }
    }

    public void unequipWeapon() {
        if (weapon != FISTS) {
            backpack.add(weapon);   // old weapon goes back to the backpack
        }
        weapon = FISTS;
        usingBothHands = false;
    }

    // exploration screen: progress and skill levels
    public String getOverview() {
        return String.format(
                "%s (%s)%n"
                        + "Level: %d | XP: %d/%d%n"
                        + "HP: %d | MP: %d | Gold: %d%n"
                        + "Strength: %.1f | Dexterity: %.1f | Agility: %.1f",
                name, getHeroType(), level, xp, xpNeededForNextLevel(),
                hp, (int) mana, (int) money,
                strength, dexterity, agility);
    }

    // battle screen: current condition and equipped gear
    public String getBattleSummary() {
        String status = isFainted ? " [FAINTED]" : "";
        return String.format(
                "%s (%s)%s%n"
                        + "Level: %d | HP: %d | MP: %d%n"
                        + "Weapon: %s%n"
                        + "Armor: %s",
                name, getHeroType(), status,
                level, hp, (int) mana,
                describeWeapon(), describeArmor());
    }

    private String describeWeapon() {
        String grip = usingBothHands && weapon.getHandsRequired() == 1 ? ", both hands" : "";
        return weapon.getName() + " (" + weapon.getWeaponDamage() + " dmg" + grip + ")";
    }

    private String describeArmor() {
        return armor == null ? "None" : armor.getName() + " (-" + armor.getDamageReduction() + " damage taken)";
    }

    // returns the weapon if it just broke, otherwise null
    public Weapon wearDownWeapon() {
        if (weapon == FISTS) {
            return null; // fists never wear out
        }
        weapon.use();
        if (weapon.isBroken()) {
            Weapon broken = weapon;
            unequipWeapon();   // broken weapon goes back to the backpack for repair
            return broken;
        }
        return null;
    }

    // returns the armor if it just broke, otherwise null
    public Armor wearDownArmor() {
        if (armor == null) {
            return null;
        }
        armor.use();
        if (armor.isBroken()) {
            Armor broken = armor;
            unequipArmor();
            return broken;
        }
        return null;
    }
}

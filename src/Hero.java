import java.util.Random;

/**
 * Represents the abstract Hero Type
 * Encompasses all common state and behavior of each type of Hero (Warrior, Sorcerer, Paladin)
 */

public abstract class Hero {
    private static final Random rand = new Random();

    // initially heroes dont have weapons so im giving them a default weapon of just fists
    static final Weapon FISTS = new Weapon("Fists", 0, 1, 75, 1);

    private final String name;
    private final Inventory backpack;

    private double mana;
    private double strength;
    private double agility;
    private double dexterity;
    private double money;

    private int xp;
    private int level;
    private int hp;

    private boolean isFainted;
    private boolean usingBothHands;

    private Armor armor;
    private Weapon weapon = FISTS;

    public Hero(String name, double mana, double strength, double agility,
                double dexterity, double money, int xp) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Hero must have a name.");
        }

        if (mana < 0 || strength < 0 || agility < 0
                || dexterity < 0 || money < 0 || xp < 0) {
            throw new IllegalArgumentException("Hero attributes cannot be negative.");
        }

        this.name = name;
        this.mana = mana;
        this.strength = strength;
        this.agility = agility;
        this.dexterity = dexterity;
        this.money = money;
        this.xp = xp;

        this.backpack = new Inventory();
        this.hp = 100; // all heroes start at level 1 and 100 hp
        this.level = 1;
        this.isFainted = false;
        this.armor = null;
    }

    public abstract HeroType getHeroType();

    public boolean attemptToDodge() {
        double chance = Math.min(0.7, agility * 0.005); // prob of dodging a hit, capping at 70% since heroes have a very high chance of dodging
        return rand.nextDouble() < chance;
    }

    // equips armor as long as the hero has it in their inventory and it isnt broken
    public void equipArmor(Armor newArmor) {
        if (newArmor == null) {
            throw new IllegalArgumentException("Armor cannot be null.");
        }

        if (newArmor.isBroken()) {
            throw new IllegalStateException(
                    newArmor.getName() + " is broken and must be repaired first."
            );
        }

        if (!backpack.contains(newArmor)) {
            throw new IllegalArgumentException(
                    "Hero does not own " + newArmor.getName() + "."
            );
        }

        backpack.remove(newArmor); // when the hero has the armor on its removed from the inventory

        if (armor != null) { // if they already had armor on it's put back into their inventory
            backpack.add(armor);
        }

        armor = newArmor;
    }

    // equips a weapon as long as a hero has it in their inventory
    // some weapons require both hands, some only one, inventory menu manages how many hands the hero chooses to use
    public void equipWeapon(Weapon newWeapon, boolean bothHands) {
        if (newWeapon == null) {
            throw new IllegalArgumentException("Weapon cannot be null.");
        }

        if (newWeapon.isBroken()) {
            throw new IllegalStateException(
                    newWeapon.getName() + " is broken and must be repaired first."
            );
        }

        if (!backpack.contains(newWeapon)) {
            throw new IllegalArgumentException(
                    "Hero does not own " + newWeapon.getName() + "."
            );
        }

        if (newWeapon.getHandsRequired() == 2) { // hero must use 2 hands if the weapon requires it
            bothHands = true;
        }

        unequipWeapon(); // if the user had a weapon already it's unequipped and put back in their inventory

        backpack.remove(newWeapon); // equip the new weapon and remove from the hero's inventory
        weapon = newWeapon;
        usingBothHands = bothHands; // delegate to inventory menu to prompt number of hands being used
    }

    public void unequipWeapon() {
        if (weapon != FISTS) {
            backpack.add(weapon);
        }

        weapon = FISTS;
        usingBothHands = false;
    }

    public void unequipArmor() {
        if (armor != null) {
            backpack.add(armor);
            armor = null;
        }
    }

    // calculates attack damage
    // using both hands grants a bonus if the weapon only requires 1
    public int calculateAttackDamage() {
        double effectiveStrength =
                usingBothHands && weapon.getHandsRequired() == 1
                        ? strength * 1.5
                        : strength;

        return (int) ((effectiveStrength + weapon.getWeaponDamage()) * 0.2); // should probably increase slightly, heroes dont do alot of damage
    }

    public int calculateSpellDamage(Spell spell) {
        return (int) (
                spell.getDamage() + (dexterity / 10000.0) * spell.getDamage() // formula from the spec, seems to work well
        );
    }

    // calculates damage taken from a monster's attack
    public int takeDamage(int rawDamage) {
        int reduction = (armor == null) ? 0 : armor.getDamageReduction(); // if a hero has armor account for damage reduction
        int actualDamage = Math.max(0, rawDamage - reduction);

        hp = Math.max(0, hp - actualDamage); // reduce hp by effective amount of damage taken, never set below 0

        return actualDamage;
    }

    // wear and tear for weapons
    public Weapon wearDownWeapon() {
        if (weapon == FISTS) {
            return null;
        }

        weapon.use(); // reduce durability by 1

        if (weapon.isBroken()) { // if the weapon breaks it's stored back in the inventory and cant be used till repaired
            Weapon broken = weapon;
            unequipWeapon();
            return broken;
        }

        return null;
    }

    // wear and tear for armor
    public Armor wearDownArmor() {
        if (armor == null) {
            return null;
        }

        armor.use(); // reduce durability by 1

        if (armor.isBroken()) { // if the armor breaks it's stored back in the inventory and cant be used till repaired
            Armor broken = armor;
            unequipArmor();
            return broken;
        }

        return null;
    }

    public void applyPotion(Potion potion) {
        int amount = potion.getEffectAmount(); // amount of desired potion effect

        for (HeroAttribute attribute : potion.getAttributes()) {
            switch (attribute) { // apply the potion to the given attribute
                case HEALTH -> hp += amount;
                case MANA -> mana += amount;
                case STRENGTH -> strength += amount;
                case DEXTERITY -> dexterity += amount;
                case AGILITY -> agility += amount;
                case DEFENSE -> {
                    // heroes don't have a defense stat given in the data need to somehow calculate this?
                    // future extension: give a defense stat in the data and add the attribute for heroes
                    // only defense currently is through armor no natural defense like monsters
                }
            }
        }
    }

    public void heal(double percentage) {
        if (percentage <= 0) {
            throw new IllegalArgumentException("Healing percentage must be greater than zero.");
        }

        hp = (int) (hp * (1 + percentage));
    }

    public void spendMana(double amount) {
        if (amount < 0 || amount > mana) {
            throw new IllegalArgumentException("Invalid mana amount.");
        }

        mana -= amount;
    }

    public void spendGold(double amount) {
        if (amount < 0 || amount > money) {
            throw new IllegalArgumentException(
                    "Cannot spend " + amount + " gold."
            );
        }

        money -= amount;
    }

    public void addGold(double gold) {
        if (gold <= 0) {
            throw new IllegalArgumentException(
                    "Gold amount must be greater than zero."
            );
        }

        money += gold;
    }

    public void gainExperience(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Experience cannot be negative.");
        }

        xp += amount;
    }


    // xp needed to level up scales w.r.t the current level (level 1 requires 10 xp, level 2 requires 30xp, and so on)
    public int xpNeededForNextLevel() {
        return level * (level + 1) * 5;
    }

    public boolean canLevelUp() {
        return xp >= xpNeededForNextLevel();
    }

    // levels up a hero and increases hp, mana, and all stats, favored stats based on hero type are increased by extra 5%
    public void levelUp() {
        level++;
        hp = level * 100;
        mana *= 1.1;

        strength *= (getHeroType() == HeroType.WARRIOR
                || getHeroType() == HeroType.PALADIN) ? 1.10 : 1.05;

        agility *= (getHeroType() == HeroType.WARRIOR
                || getHeroType() == HeroType.SORCERER) ? 1.10 : 1.05;

        dexterity *= (getHeroType() == HeroType.SORCERER
                || getHeroType() == HeroType.PALADIN) ? 1.10 : 1.05;
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("HP cannot be negative.");
        }

        hp = amount;
    }

    public double getMana() {
        return mana;
    }

    public void setMana(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Mana cannot be negative.");
        }

        mana = amount;
    }

    public double getStrength() {
        return strength;
    }

    public void setStrength(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Strength cannot be negative.");
        }

        strength = amount;
    }

    public double getAgility() {
        return agility;
    }

    public double getDexterity() {
        return dexterity;
    }

    public double getMoney() {
        return money;
    }

    public int getXp() {
        return xp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int amount) {
        if (amount < 1) {
            throw new IllegalArgumentException("Level must be at least 1.");
        }

        level = amount;
    }

    public Inventory getBackpack() {
        return backpack;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public Armor getArmor() {
        return armor;
    }

    public boolean getIsFainted() {
        return isFainted;
    }

    public void setIsFainted(boolean isFainted) {
        this.isFainted = isFainted;
    }

    // Information displays

    public String getOverview() {
        return String.format(
                "%s (%s)%n"
                        + "Level: %d | XP: %d/%d%n"
                        + "HP: %d | MP: %d | Gold: %d%n"
                        + "Strength: %.1f | Dexterity: %.1f | Agility: %.1f",
                name, getHeroType(), level, xp, xpNeededForNextLevel(),
                hp, (int) mana, (int) money,
                strength, dexterity, agility
        );
    }

    public String getBattleSummary() {
        String status = isFainted ? " [FAINTED]" : "";

        return String.format(
                "%s (%s)%s%n"
                        + "Level: %d | HP: %d | MP: %d%n"
                        + "Weapon: %s%n"
                        + "Armor: %s",
                name, getHeroType(), status,
                level, hp, (int) mana,
                describeWeapon(), describeArmor()
        );
    }

    private String describeWeapon() {
        String grip = usingBothHands && weapon.getHandsRequired() == 1
                ? ", both hands"
                : "";

        return weapon.getName() + " ("
                + weapon.getWeaponDamage() + " dmg" + grip + ")";
    }

    private String describeArmor() {
        return armor == null
                ? "None"
                : armor.getName() + " (-" + armor.getDamageReduction()
                + " damage taken)";
    }
}
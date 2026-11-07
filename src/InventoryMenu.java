import java.util.List;

public class InventoryMenu {
    private final Controller console;

    public InventoryMenu(Controller console) {
        this.console = console;
    }

    // each method returns true if something happened (so a battle turn is used up)

    public boolean equipWeapon(Hero hero) {
        List<Weapon> weapons = hero.getBackpack().getItemsOfType(Weapon.class);
        if (weapons.isEmpty()) {
            System.out.println(hero.getName() + " has no weapons to equip.");
            return false;
        }

        Weapon weapon = console.selectItem("Choose a weapon to equip:", weapons);
        if (weapon == null) {
            return false;
        }

        if (weapon.isBroken()) {
            System.out.println(weapon.getName() + " is broken. Repair it at a market first.");
            return false;
        }

        if (weapon.getHandsRequired() == 1 && console.askYesNo("Hold it with both hands for extra strength?")) {
            hero.equipWeapon(weapon, true);
        } else {
            weapon.equip(hero); // default grip: two-handed weapons use both hands, one-handed use one
        }

        System.out.println(hero.getName() + " equipped " + weapon.getName() + "!");
        return true;
    }

    public boolean equipArmor(Hero hero) {
        List<Armor> armors = hero.getBackpack().getItemsOfType(Armor.class);
        if (armors.isEmpty()) {
            System.out.println(hero.getName() + " has no armor to equip.");
            return false;
        }

        Armor armor = console.selectItem("Choose armor to equip:", armors);
        if (armor == null) {
            return false;
        }

        if (armor.isBroken()) {
            System.out.println(armor.getName() + " is broken. Repair it at a market first.");
            return false;
        }

        armor.equip(hero);
        System.out.println(hero.getName() + " equipped " + armor.getName() + "!");
        return true;
    }

    public boolean usePotion(Hero hero) {
        List<Potion> potions = hero.getBackpack().getItemsOfType(Potion.class);
        if (potions.isEmpty()) {
            System.out.println(hero.getName() + " has no potions.");
            return false;
        }

        Potion potion = console.selectItem("Choose a potion to use:", potions);
        if (potion == null) {
            return false;
        }

        potion.use(hero);
        hero.getBackpack().remove(potion); // single-use
        System.out.println(hero.getName() + " used " + potion.getName() + "!");
        return true;
    }

    // exploration only: gear must be unequipped before it can be sold
    public void unequipWeapon(Hero hero) {
        Weapon current = hero.getWeapon();
        if (current == Hero.FISTS) {
            System.out.println(hero.getName() + " has no weapon equipped.");
            return;
        }
        current.unequip(hero);
        System.out.println(hero.getName() + " put their weapon back in the backpack.");
    }

    public void unequipArmor(Hero hero) {
        Armor current = hero.getArmor();
        if (current == null) {
            System.out.println(hero.getName() + " has no armor equipped.");
            return;
        }
        current.unequip(hero);
        System.out.println(hero.getName() + " took off their armor.");
    }
}
/**
 * Used to define the behavior of items which are Equipable
 * Hero's can equip and unequip weapons
 */

public interface Equipable {
    void equip(Hero hero); // add item to hero
    void unequip(Hero hero);
}
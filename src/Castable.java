/**
 * Represents spells that can be cast.
 * Used by Spell to define how spells are cast.
 */

public interface Castable {
    void cast(Hero hero, Monster monster);
}
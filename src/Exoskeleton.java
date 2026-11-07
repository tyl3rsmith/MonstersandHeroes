/**
 * Used to define the type Exoskeleton
 * All monsters essentially reuse Parent logic
 * The data already provides the functionality to account for favored attributes
 * This is just useful for future extensions of Exoskeleton
 */

public class Exoskeleton extends Monster {
    public Exoskeleton(String name, int level, int hp, int baseDamage, int defense, double dodgeChance) {
        super(name, level, hp, baseDamage, defense, dodgeChance);
    }
}
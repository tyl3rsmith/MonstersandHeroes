/**
 * Used to define the type Dragon
 * All monsters essentially reuse Parent logic
 * The data already provides the functionality to account for favored attributes
 * This is just useful for future extensions of Dragons
 */

public class Dragon extends Monster {
    public Dragon(String name, int level, int hp, int baseDamage, int defense, double dodgeChance) {
        super(name, level, hp, baseDamage, defense, dodgeChance);
    }
}
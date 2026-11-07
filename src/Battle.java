import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages battles between a group of heroes and a group of monsters.
 * Handles turn-based combat, including hero actions, monster attacks,
 * damage calculations, dodging, and determining the outcome of the battle.
 * Also manages battle rewards and the consequences of victory or defeat.
 */

public class Battle {
    private final MonsterGroup monsters;
    private final HeroGroup heroes;
    private final Controller console;
    private final Random rand = new Random();
    private final int monsterLevel;
    private final int monsterCount;
    private final InventoryMenu inventoryMenu;
    private final Runnable showMap; // using this to pass along the display map function so we can display without knowing how to


    public Battle(MonsterGroup monsters, HeroGroup heroes, Controller console, Runnable showMap) {
        this.monsters = monsters;
        this.heroes = heroes;
        this.console = console;

        // saving these now since the group empties as monsters die
        this.monsterLevel = heroes.getHighestLevelHero(); // monster level = highest hero level
        this.monsterCount = monsters.getNumMonsters();

        this.inventoryMenu = new InventoryMenu(console);
        this.showMap = showMap;
    }

    // fainted heroes stay in the party, so we filter them out whenever someone needs to act
    private List<Hero> getLivingHeroes() {
        List<Hero> alive = new ArrayList<>();
        for (Hero hero : heroes.getHeroes()) {
            if (!hero.getIsFainted()) {
                alive.add(hero);
            }
        }
        return alive;
    }

    public boolean isHeroesFainted() {
        return getLivingHeroes().isEmpty();
    }


    // flag used to mark heroes as fainted when they hit 0 hp
    private boolean isHeroFainted(Hero hero) {
        if (hero.getHp() == 0) {
            hero.setIsFainted(true);
            return true;
        }
        return false;
    }

    private boolean isMonsterFainted(Monster monster){
        return monster.getHp() == 0;
    }

    public boolean isMonstersFainted() {
        return monsters.getNumMonsters() == 0;
    }

    // after heroes win, fainted heroes come back with half hp and mana (and no rewards)
    public void reviveFaintedHeroes() {
        for (Hero hero : heroes.getHeroes()) {
            if (hero.getIsFainted()) {
                hero.setIsFainted(false);
                hero.setHp(Math.max(1, hero.getLevel() * 100 / 2));
                hero.setMana(hero.getMana() / 2);
            }
        }
    }

    // living heroes regain 10% hp and mana each round
    public void endOfRound() {
        for (Hero hero : getLivingHeroes()) {
            hero.heal(0.1);
            hero.setMana(hero.getMana() * 1.1);
        }
    }

    private boolean monsterDodges(Monster monster) {
        return rand.nextDouble() < monster.getDodgeChance() / 100.0;
    }

    private void removeIfDefeated(Monster monster) {
        if (isMonsterFainted(monster)) {
            System.out.println(monster.getName() + " has fainted!");
            monsters.removeMonster(monster);
        }
    }

    // battles happen in turns each hero as long as there alive acts separately
    public void heroesTurn() {
        for (Hero hero : getLivingHeroes()) {
            if (isMonstersFainted()) {
                return; // last monster already died, stop the round early
            }
            takeHeroTurn(hero);
        }
    }

    // prompts the hero to perform some action until they utilize the turn (i.e. failing to equip armor doesn't consume a turn)
    private void takeHeroTurn(Hero hero) {
        boolean turnUsed = false;
        while (!turnUsed) {
            char choice = console.getHeroBattleChoice(hero);

            switch (choice) {
                case 'A': turnUsed = handleHeroAttack(hero); break;
                case 'S': turnUsed = handleHeroSpellCast(hero); break;
                case 'P': turnUsed = handleHeroPotionUse(hero); break;
                case 'W': turnUsed = handleHeroEquipWeapon(hero); break;
                case 'E': turnUsed = handleHeroEquipArmor(hero); break;
                case 'I': displayStatistics(); break;
                case 'M': showMap.run(); break;
                case 'Q': quitGame(); break; // immediately exit
            }
        }
    }

    // each monster randomly selects a non-fainted hero to attack
    public void monsterTurn() {
        for (int i = 0; i < monsters.getNumMonsters(); i++) {
            List<Hero> alive = getLivingHeroes();
            if (alive.isEmpty()) {
                return; // everyone fainted, battle is over
            }

            Monster monster = monsters.getMonsterByIndex(i);
            Hero target = alive.get(rand.nextInt(alive.size()));

            if (target.attemptToDodge()) {
                System.out.println(target.getName() + " dodged " + monster.getName() + "'s attack!");
                continue;
            }

            int dealt = target.takeDamage((int) (monster.getBaseDamage() * 0.5)); // monsters do a lot of damage gonna reduce by half maybe should tweak more
            System.out.println(monster.getName() + " attacked " + target.getName() + " for " + dealt + " damage!");

            // armor only wears down when a hit actually lands
            Armor brokenArmor = target.wearDownArmor();
            if (brokenArmor != null) {
                System.out.println(target.getName() + "'s " + brokenArmor.getName() + " broke!");
            }

            if (isHeroFainted(target)) {
                System.out.println(target.getName() + " has fainted!");
            }
        }
    }


    private void displayStatistics() {
        console.statistics(monsters, heroes);
    }


    // handles logic for attacking a monster and returns true if the hero didn't back out of their turn
    private boolean handleHeroAttack(Hero hero) {
        int targetIndex = console.selectMonster(monsters);
        if (targetIndex < 0) {
            return false;
        }
        Monster target = monsters.getMonsterByIndex(targetIndex);

        if (monsterDodges(target)) { // monster has a chance to dodge the attack, however it's really low should probably tweak this
            System.out.println(target.getName() + " dodged " + hero.getName() + "'s attack!");
        } else {
            int dealt = target.takeDamage(hero.calculateAttackDamage());
            System.out.println(hero.getName() + " attacked " + target.getName() + " for " + dealt + " damage!");
        }

        // hit or miss, every use of a weapon cause wear and tear
        Weapon broken = hero.wearDownWeapon();
        if (broken != null) {
            System.out.println(hero.getName() + "'s " + broken.getName()
                    + " broke! Repair it at a market. Back to fists for now.");
        }

        removeIfDefeated(target);
        return true;
    }

    // first select a hero to use
    // then select a spell from the heroes' inventory to be cast (fire, ice, lightning)
    // then select a monster as the target
    // apply the spell debuff to the target monster
    // check if any monsters fainted if they did remove them from the current battle
    private boolean handleHeroSpellCast(Hero hero) {
        List<Spell> spells = hero.getBackpack().getItemsOfType(Spell.class);
        if (spells.isEmpty()) {
            System.out.println(hero.getName() + " has no spells.");
            return false;
        }

        Spell spell = console.selectItem("Choose a spell to cast:", spells);
        if (spell == null) {
            return false; // cancelled
        }
        if (hero.getMana() < spell.getManaCost()) {
            System.out.println("Not enough mana. " + spell.getName() + " costs "
                    + spell.getManaCost() + ", " + hero.getName() + " has " + (int) hero.getMana() + ".");
            return false;
        }

        int targetIndex = console.selectMonster(monsters);
        if (targetIndex < 0) {
            return false;
        }
        Monster target = monsters.getMonsterByIndex(targetIndex);

        hero.spendMana(spell.getManaCost());
        hero.getBackpack().remove(spell); // spells are single-use

        if (monsterDodges(target)) {
            System.out.println(target.getName() + " dodged " + hero.getName() + "'s " + spell.getName() + "!");
        } else {
            spell.cast(hero, target);
        }
        removeIfDefeated(target);
        return true;
    }

    // inventory menu has the logic for equipping items in the hero's inventory so we can delegate
    private boolean handleHeroEquipWeapon(Hero hero) { return inventoryMenu.equipWeapon(hero); }
    private boolean handleHeroEquipArmor(Hero hero)  { return inventoryMenu.equipArmor(hero); }
    private boolean handleHeroPotionUse(Hero hero)   { return inventoryMenu.usePotion(hero); }

    // the menus live in InventoryMenu so exploration can use them too
    public void awardRewards() {
        for (Hero hero : getLivingHeroes()) {
            hero.addGold(monsterLevel * 100.0);
            hero.gainExperience(monsterCount * 2);
            System.out.println(hero.getName() + " earned " + (monsterLevel * 100) + " gold and "
                    + (monsterCount * 2) + " XP!");
        }
    }

    private void quitGame() {
        console.quitGameText(heroes);
        System.exit(0);
    }

}
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Battle {
    private MonsterGroup monsters;
    private HeroGroup heroes;
    private Controller console;
    private Random rand = new Random();
    private final int monsterLevel;
    private final int monsterCount;
    private final InventoryMenu inventoryMenu;
    private final Runnable showMap; // using this to pass along the display map function


    public Battle(MonsterGroup monsters, HeroGroup heroes, Runnable showMap) {
        this.monsters = monsters;
        this.heroes = heroes;
        this.console = new Controller();
        this.monsterLevel = heroes.getHighestLevelHero(); // monster level = highest hero level
        this.monsterCount = monsters.getNumMonsters();
        this.inventoryMenu = new InventoryMenu(console);
        this.showMap = showMap;
    }

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

    public void reviveFaintedHeroes() {
        for (Hero hero : heroes.getHeroes()) {
            if (hero.getIsFainted()) {
                hero.setIsFainted(false);
                hero.setHp(Math.max(1, hero.getLevel() * 100 / 2));
                hero.setMana(hero.getMana() / 2);
            }
        }
    }

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

    public void heroesTurn() {
        for (Hero hero : getLivingHeroes()) {
            if (isMonstersFainted()) {
                return; // last monster already died, stop the round early
            }
            takeHeroTurn(hero);
        }
    }

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

            int dealt = target.takeDamage((int) (monster.getBaseDamage() * 0.5)); // monsters do a lot of damage gonna reduce by half
            System.out.println(monster.getName() + " attacked " + target.getName() + " for " + dealt + " damage!");

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


    private boolean handleHeroAttack(Hero hero) {
        Monster target = monsters.getMonsterByIndex(console.selectMonster(monsters));

        if (monsterDodges(target)) {
            System.out.println(target.getName() + " dodged " + hero.getName() + "'s attack!");
        } else {
            int dealt = target.takeDamage(hero.calculateAttackDamage());
            System.out.println(hero.getName() + " attacked " + target.getName() + " for " + dealt + " damage!");
        }

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

        Monster target = monsters.getMonsterByIndex(console.selectMonster(monsters));

        hero.spendMana(spell.getManaCost());
        hero.getBackpack().remove(spell); // spells are single-use

        if (monsterDodges(target)) {
            System.out.println(target.getName() + " dodged " + hero.getName() + "'s " + spell.getName() + "!");
        } else {
            int dealt = target.takeDamage(hero.calculateSpellDamage(spell));
            System.out.println(hero.getName() + " cast " + spell.getName() + " on " + target.getName()
                    + " for " + dealt + " damage!");
            System.out.println(spell.applyEffect(target));
        }
        removeIfDefeated(target);
        return true;
    }

    // first select a hero to use
    // then select a potion from the heroes' inventory
    // apply the potion effect to the specific hero attribute
//    private boolean handleHeroPotionUse(Hero hero) {
//        List<Potion> potions = hero.getBackpack().getItemsOfType(Potion.class);
//        if (potions.isEmpty()) {
//            System.out.println(hero.getName() + " has no potions.");
//            return false;
//        }
//
//        Potion potion = console.selectItem("Choose a potion to use:", potions);
//        if (potion == null) {
//            return false;
//        }
//
//        hero.applyPotion(potion);
//        hero.getBackpack().remove(potion); // single-use
//        System.out.println(hero.getName() + " used " + potion.getName() + "!");
//        return true;
//    }
//
//    private boolean handleHeroEquipWeapon(Hero hero) {
//        List<Weapon> weapons = hero.getBackpack().getItemsOfType(Weapon.class);
//        if (weapons.isEmpty()) {
//            System.out.println(hero.getName() + " has no weapons to equip.");
//            return false;
//        }
//
//        Weapon weapon = console.selectItem("Choose a weapon to equip:", weapons);
//        if (weapon == null) {
//            return false;
//        }
//
//        boolean bothHands = weapon.getHandsRequired() == 2 || console.askYesNo("Hold it with both hands for extra strength?");
//        hero.equipWeapon(weapon, bothHands);
//        System.out.println(hero.getName() + " equipped " + weapon.getName() + "!");
//        return true;
//    }
//
//    private boolean handleHeroEquipArmor(Hero hero) {
//        List<Armor> armors = hero.getBackpack().getItemsOfType(Armor.class);
//        if (armors.isEmpty()) {
//            System.out.println(hero.getName() + " has no armor to equip.");
//            return false;
//        }
//
//        Armor armor = console.selectItem("Choose armor to equip:", armors);
//        if (armor == null) {
//            return false;
//        }
//
//        hero.equipArmor(armor);
//        System.out.println(hero.getName() + " equipped " + armor.getName() + "!");
//        return true;
//    }

    private boolean handleHeroEquipWeapon(Hero hero) { return inventoryMenu.equipWeapon(hero); }
    private boolean handleHeroEquipArmor(Hero hero)  { return inventoryMenu.equipArmor(hero); }
    private boolean handleHeroPotionUse(Hero hero)   { return inventoryMenu.usePotion(hero); }

    public void awardRewards() {
        for (Hero hero : getLivingHeroes()) {
            hero.addGold(monsterLevel * 100.0);
            hero.gainExperience(monsterCount * 2);
            System.out.println(hero.getName() + " earned " + (monsterLevel * 100) + " gold and "
                    + (monsterCount * 2) + " XP!");
        }
    }

    private void quitGame() {
        console.quitGame(heroes);
        System.exit(0);
    }

}

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate Group of Heroes
 * Stores all the logic for managing heroes as a group
 * The group must be size 1 to 3 inclusive
 */

public class HeroGroup {
    public static final int MAX_HERO_GROUP_SIZE = 3;

    private final List<Hero> heroes;

    public HeroGroup() {
        this.heroes = new ArrayList<>();
    }

    public void addHero(Hero hero) {
        if (hero == null) {
            throw new IllegalArgumentException("Hero cannot be null.");
        }

        if (heroes.size() >= MAX_HERO_GROUP_SIZE) {
            throw new IllegalStateException("Party is full.");
        }

        heroes.add(hero);
    }

    public List<Hero> getHeroes() {
        return Collections.unmodifiableList(heroes);
    }

    public Hero getHeroByIndex(int index) {
        if (index < 0 || index >= heroes.size()) {
            throw new IndexOutOfBoundsException(
                    "Invalid hero index for a group of size " + getNumHeroes() + "."
            );
        }

        return heroes.get(index);
    }

    public int getNumHeroes() {
        return heroes.size();
    }

    // highest level hero is used to determine the level of monsters generated
    public int getHighestLevelHero() {
        if (heroes.isEmpty()) {
            return -1;
        }

        int highestLevel = 0;

        for (Hero hero : heroes) {
            if (hero.getLevel() > highestLevel) {
                highestLevel = hero.getLevel();
            }
        }

        return highestLevel;
    }

    // these are used to print out stats at the end of the game
    public int getHighestXP() {
        if (heroes.isEmpty()) {
            return -1;
        }

        int highestXP = 0;

        for (Hero hero : heroes) {
            if (hero.getXp() > highestXP) {
                highestXP = hero.getXp();
            }
        }

        return highestXP;
    }

    public double getHighestGold() {
        if (heroes.isEmpty()) {
            return -1;
        }

        double highestGold = 0;

        for (Hero hero : heroes) {
            if (hero.getMoney() > highestGold) {
                highestGold = hero.getMoney();
            }
        }

        return highestGold;
    }

    public double getHighestMana() {
        if (heroes.isEmpty()) {
            return -1;
        }

        double highestMana = 0;

        for (Hero hero : heroes) {
            if (hero.getMana() > highestMana) {
                highestMana = hero.getMana();
            }
        }

        return highestMana;
    }

    // levels up the heroes as an aggregate, each hero knows how to level themselves up so we can delegate
    public void levelUpHeroes() {
        for (Hero hero : heroes) {
            while (hero.canLevelUp()) {
                hero.levelUp();

                System.out.println(
                        hero.getName() + " reached level " + hero.getLevel() + "!"
                );
            }
        }
    }

    // useful display methods showing information about the hero group during the main menu and during the battle
    public void displayHeroGroupInformation() {
        System.out.println("\n========== PARTY INFORMATION ==========");

        int i = 1;

        for (Hero hero : heroes) {
            System.out.println("\nHero " + i++ + ": " + hero.getOverview());
            System.out.println("---------------------------------------");
        }
    }

    public void displayBattleInformation() {
        System.out.println("\n========== HEROES ==========");

        int i = 1;

        for (Hero hero : heroes) {
            System.out.println("\nHero " + i++ + ": " + hero.getBattleSummary());
            System.out.println("---------------------------------------");
        }
    }
}
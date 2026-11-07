import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HeroGroup {
    private final List<Hero> heroes;
    public static final int MAX_HERO_GROUP_SIZE = 3;

    public HeroGroup() {
        this.heroes = new ArrayList<>();
    }

    public void addHero(Hero hero) {
        if (hero == null) {
            throw new IllegalArgumentException("Hero cannot be null");
        }

        if (heroes.size() >= MAX_HERO_GROUP_SIZE) {
            throw new IllegalStateException("party is full");
        }

        heroes.add(hero);
    }

    public List<Hero> getHeroes() {
        return Collections.unmodifiableList(heroes); // read only version of hero list
    }

    public Hero getHeroByIndex(int index) {
        if (index < 0 || index >= heroes.size()) {
            throw new ArrayIndexOutOfBoundsException("Invalid index provided for a Hero Group of size: " + getNumHeroes());
        }

        return heroes.get(index);
    }

    public int getNumHeroes() {
        return heroes.size();
    }


    public int getHighestLevelHero() {
        if (heroes.isEmpty()) {
            return -1;
        }

        int res = -1;
        for (Hero hero : heroes) {
            if (hero.getLevel() > res) {
                res = hero.getLevel();
            }
        }
        return res;
    }


    public int getHighestXP() {
        if (heroes.isEmpty()) {
            return -1;
        }

        int res = 0;
        for (Hero hero : heroes) {
            if (hero.getXp() > res) {
                res = hero.getXp();
            }
        }
        return res;
    }

    public double getHighestGold() {
        if (heroes.isEmpty()) {
            return -1;
        }

        double res = 0;
        for (Hero hero : heroes) {
            if (hero.getMoney() > res) {
                res = hero.getMoney();
            }
        }
        return res;
    }

    public double getHighestMana() {
        if (heroes.isEmpty()) {
            return -1;
        }

        double res = 0;
        for (Hero hero : heroes) {
            if (hero.getMana() > res) {
                res = hero.getMana();
            }
        }
        return res;
    }

    public void levelUpHeroes() {
        for (Hero hero : heroes) {
            while (hero.canLevelUp()) {
                hero.levelUp();
                System.out.println(hero.getName() + " reached level " + hero.getLevel() + "!");
            }
        }
    }

    public void displayHeroGroupInformation() {
        System.out.println("\n========== PARTY INFORMATION ==========");
        int i = 1;
        for (Hero hero : heroes) {
            System.out.println("\nHero " + (i++) + ": " + hero.getOverview());
            System.out.println("---------------------------------------");
        }
    }

    public void displayBattleInformation() {
        System.out.println("\n========== HEROES ==========");
        int i = 1;
        for (Hero hero : heroes) {
            System.out.println("\nHero " + (i++) + ": " + hero.getBattleSummary());
            System.out.println("---------------------------------------");
        }
    }



}

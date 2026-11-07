import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class MonsterGroup {
    private List<Monster> monsters;
    private MonsterFactory monsterFactory;
    private int numMonsters;

    public MonsterGroup() {
        this.monsters = new ArrayList<>();
        this.monsterFactory = new MonsterFactory();
    }

    public MonsterGroup(List<Monster> monsters, MonsterFactory monsterFactory) {
        if (monsterFactory == null && monsters == null) {
            throw new IllegalArgumentException("Invalid constructor arguments provided");
        }

        this.monsters = monsters;
        this.monsterFactory = monsterFactory;
    }

    public MonsterGroup clone() {
        List<Monster> newMonsters = new ArrayList<>(monsters);
        MonsterFactory monsterFactory = new MonsterFactory();

        MonsterGroup newGroup = new MonsterGroup(newMonsters, monsterFactory);
        newGroup.numMonsters = newMonsters.size();
        return newGroup;
    }

    public int getNumMonsters() {
        return numMonsters;
    }

    public boolean removeMonster(Monster monster) {
        if (monster != null && monsters.remove(monster)) {
            numMonsters--;
            return true;
        }

        return false;
    }

    public Monster getMonsterByIndex(int index) {
        if (index < 0 || index >= numMonsters) {
            throw new IllegalArgumentException("Invalid index");
        }
        return monsters.get(index);
    }

    public List<Monster> getMonsters() {
        return Collections.unmodifiableList(monsters);
    }

    public void addMonster(Monster monster) {
        if (monster == null) {
            throw new IllegalArgumentException("Hero cannot be null");
        }

        if (monsters.size() >= HeroGroup.MAX_HERO_GROUP_SIZE) {
            throw new IllegalStateException("party is full");
        }

        monsters.add(monster);
        numMonsters++;
    }

    public MonsterGroup createRandomMonsterGroup(HeroGroup heroes) {
        int monsterLevel = heroes.getHighestLevelHero();
        int monsterCount = heroes.getNumHeroes();

        MonsterGroup monsterGroup = new MonsterGroup();

        for (int i = 0; i < monsterCount; i++) {
            // Create a monster of the selected type with monsterLevel as its level
            MonsterType type = Monster.getRandomMonsterType();
            Monster monster = monsterFactory.createRandomMonster(type, monsterLevel);
            monsterGroup.addMonster(monster);
        }

        numMonsters = monsterCount;
        return monsterGroup;
    }

    public void displayMonsterGroupInformation() {
        System.out.println("\n========== MONSTERS INFORMATION ==========");
        int i = 1;

        for (Monster monster : this.monsters) {
            System.out.println("\nMonster " + (i++) + ": " + monster.getName());
            System.out.println("Level: " + monster.getLevel());
            System.out.println("HP: " + monster.getHp());
            System.out.println("Base Damage: " + monster.getBaseDamage());
            System.out.println("Defense: " + monster.getDefense());
            System.out.println("Dodge Chance: " + monster.getDodgeChance());
            System.out.println("---------------------------------------");
        }
    }

}

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate Group of Monsters
 * Stores all the logic for managing monsters as a group
 * The group must be equal to the size of the hero group
 */

public class MonsterGroup {
    private final List<Monster> monsters;
    private final MonsterFactory monsterFactory;

    public MonsterGroup() {
        this.monsters = new ArrayList<>();
        this.monsterFactory = new MonsterFactory();
    }

    public int getNumMonsters() {
        return monsters.size();
    }

    public boolean removeMonster(Monster monster) {
        return monster != null && monsters.remove(monster);
    }

    public Monster getMonsterByIndex(int index) {
        if (index < 0 || index >= monsters.size()) {
            throw new IndexOutOfBoundsException("Invalid monster index.");
        }

        return monsters.get(index);
    }

    public List<Monster> getMonsters() {
        return Collections.unmodifiableList(monsters);
    }

    public void addMonster(Monster monster) {
        if (monster == null) {
            throw new IllegalArgumentException("Monster cannot be null.");
        }

        if (monsters.size() >= HeroGroup.MAX_HERO_GROUP_SIZE) {
            throw new IllegalStateException("Monster group is full.");
        }

        monsters.add(monster);
    }

    // creates a random group of monsters during the battle
    // delegates to monster factory to get random monsters
    public MonsterGroup createRandomMonsterGroup(HeroGroup heroes) {
        if (heroes == null || heroes.getNumHeroes() == 0) {
            throw new IllegalArgumentException("Hero group cannot be empty.");
        }

        int monsterLevel = heroes.getHighestLevelHero();
        int monsterCount = heroes.getNumHeroes();

        MonsterGroup monsterGroup = new MonsterGroup();

        for (int i = 0; i < monsterCount; i++) {
            // Create a monster with the highest hero's level
            MonsterType type = Monster.getRandomMonsterType();
            Monster monster = monsterFactory.createRandomMonster(type, monsterLevel);

            monsterGroup.addMonster(monster);
        }

        return monsterGroup;
    }

    public void displayMonsterGroupInformation() {
        System.out.println("\n========== MONSTERS INFORMATION ==========");

        int i = 1;

        for (Monster monster : monsters) {
            System.out.println("\nMonster " + i++ + ": " + monster.getName());
            System.out.println("Level: " + monster.getLevel());
            System.out.println("HP: " + monster.getHp());
            System.out.println("Base Damage: " + monster.getBaseDamage());
            System.out.println("Defense: " + monster.getDefense());
            System.out.println("Dodge Chance: " + monster.getDodgeChance());
            System.out.println("---------------------------------------");
        }
    }
}
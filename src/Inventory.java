import java.util.ArrayList;
import java.util.List;

/**
 * Inventory
 * manages the inventory of a hero
 */

public class Inventory {
    private List<Item> backpack;

    public Inventory() {
        this.backpack = new ArrayList<>();
    }

    public boolean contains(Item item) {
        return item != null && backpack.contains(item);
    }

    public void remove(Item item) {
        if (item != null) {
            backpack.remove(item);
        }
    }

    public void add(Item item) {
        if (item != null) {
            backpack.add(item);
        }
    }

    public Item get(int index) {
        return backpack.get(index);
    }

    // gets items of a specified class type in the heroes inventory i.e. Weapon.class gives all weapon items
    public <T extends Item> List<T> getItemsOfType(Class<T> type) {
        List<T> result = new ArrayList<>();
        for (Item item : backpack) {
            if (type.isInstance(item)) {
                result.add(type.cast(item));
            }
        }
        return result;
    }

    // default display for inventory
    @Override
    public String toString() {
        if (backpack.isEmpty()) {
            return "Inventory is empty.";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < backpack.size(); i++) {
            sb.append(i + 1).append(". ").append(backpack.get(i)).append("\n");
        }
        return sb.toString().trim();
    }
}

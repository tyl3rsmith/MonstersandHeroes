import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Market Items
 * Manages the aggregate of items a market has
 */

public class MarketItems {
    private final List<Item> marketItems;

    public MarketItems() {
        this.marketItems = new ArrayList<>();
    }

    public boolean addItem(Item item) {
        return item != null && marketItems.add(item);
    }

    public List<Item> getInventory() {
        return Collections.unmodifiableList(marketItems);
    }

    public boolean contains(Item item) {
        return item != null && marketItems.contains(item);
    }

    public boolean removeItem(Item item) {
        return item != null && marketItems.remove(item);
    }
}
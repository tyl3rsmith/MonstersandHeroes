import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * Factory Class for Generating Market Supply of Items
 * Abstracts the logic for generating a Market, the game and grid knows nothing about how this is done
 * Uses the item factory to generate all the items and randomly allocates to markets
 */

public class MarketFactory {
    private static final int MIN_ITEMS = 3; // each market will have between 3-5 items at random
    private static final int MAX_ITEMS = 5;

    private final ItemFactory itemFactory;
    private final Deque<Item> pool = new ArrayDeque<>();
    private final Random rand = new Random();

    public MarketFactory(ItemFactory itemFactory) {
        this.itemFactory = itemFactory;
    }

    public Market createMarket() {
        int count = MIN_ITEMS + rand.nextInt(MAX_ITEMS - MIN_ITEMS + 1);
        List<Item> picked = new ArrayList<>();

        while (picked.size() < count) {
            if (pool.isEmpty()) { // refill the item pool if all items have been used
                refillPool();
            }
            Item next = pool.poll();
            // after a reshuffle the same item can come up twice in one market so we need to skip it
            if (!containsName(picked, next)) {
                picked.add(next);
            }
        }
        return new Market(picked);
    }

    private void refillPool() {
        List<Item> all = itemFactory.getAllItems();
        Collections.shuffle(all, rand); // randomly shuffle the items and add to the pool of items for the market to use
        for (Item item : all) {
            pool.add(item.copy());
        }
    }

    // helper method to determine if item has been picked already
    private boolean containsName(List<Item> items, Item candidate) {
        for (Item item : items) {
            if (item.getName().equals(candidate.getName())) {
                return true;
            }
        }
        return false;
    }
}
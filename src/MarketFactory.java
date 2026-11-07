import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

public class MarketFactory {
    private static final int MIN_ITEMS = 3;
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
            if (pool.isEmpty()) {
                refillPool();
            }
            Item next = pool.poll();
            // after a reshuffle the same item can come up twice in one market; skip it
            if (!containsName(picked, next)) {
                picked.add(next);
            }
        }
        return new Market(picked);
    }

    private void refillPool() {
        List<Item> all = itemFactory.getAllItems();
        Collections.shuffle(all, rand);
        for (Item item : all) {
            pool.add(item.copy());
        }
    }

    private boolean containsName(List<Item> items, Item candidate) {
        for (Item item : items) {
            if (item.getName().equals(candidate.getName())) {
                return true;
            }
        }
        return false;
    }
}
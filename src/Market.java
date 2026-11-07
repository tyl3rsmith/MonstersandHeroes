import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

enum TransactionResult {
    SUCCESS("Transaction complete!"),
    NOT_IN_STOCK("The market doesn't have that item."),
    NOT_OWNED("You don't own that item."),
    LEVEL_TOO_LOW("You aren't a high enough level to buy that."),
    NOT_ENOUGH_GOLD("You don't have enough gold."),
    NOT_REPAIRABLE("That item can't be repaired."),
    ALREADY_FULL("That item doesn't need repairs.");

    private final String message;

    TransactionResult(String message) { this.message = message; }

    public String getMessage() { return message; }
}

public class Market {
    private final MarketItems inventory = new MarketItems();

    public Market(List<Item> items) {
        for (Item item : items) {
            inventory.addItem(item);
        }
    }

    public MarketItems getMarketItems() {
        return inventory;
    }

    public TransactionResult buyItem(Hero hero, Item item) {
        if (!inventory.contains(item)) {
            return TransactionResult.NOT_IN_STOCK;
        }
        if (hero.getLevel() < item.getLevel()) {
            return TransactionResult.LEVEL_TOO_LOW;
        }
        if (hero.getMoney() < item.getPrice()) {
            return TransactionResult.NOT_ENOUGH_GOLD;
        }

        hero.spendGold(item.getPrice());
        inventory.removeItem(item);
        hero.getBackpack().add(item);
        return TransactionResult.SUCCESS;
    }

    public TransactionResult sellItem(Hero hero, Item item) {
        if (!hero.getBackpack().contains(item)) {
            return TransactionResult.NOT_OWNED;
        }

        hero.getBackpack().remove(item);
        hero.addGold(item.getSellPrice());
        inventory.addItem(item); // other heroes, or this one later, can buy it
        return TransactionResult.SUCCESS;
    }

    public TransactionResult repairItem(Hero hero, Item item) {
        if (!hero.getBackpack().contains(item)) {
            return TransactionResult.NOT_OWNED;
        }
        if (!(item instanceof Repairable)) {
            return TransactionResult.NOT_REPAIRABLE;
        }

        Repairable repairable = (Repairable) item;
        if (!repairable.needsRepair()) {
            return TransactionResult.ALREADY_FULL;
        }
        if (hero.getMoney() < repairable.getRepairCost()) {
            return TransactionResult.NOT_ENOUGH_GOLD;
        }

        hero.spendGold(repairable.getRepairCost());
        repairable.repair();
        return TransactionResult.SUCCESS;
    }
}

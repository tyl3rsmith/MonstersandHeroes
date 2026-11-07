import java.util.List;

/**
 * Market
 * represents each unique market in the game
 * heroes can buy, sell, and repair stuff at a market
 * each market has a unique supply of items offered
 * uses TransactionResult to display the different messages based on the outcome of the transaction
 */

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
        if (!(item instanceof Repairable repairable)) {
            return TransactionResult.NOT_REPAIRABLE;
        }

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
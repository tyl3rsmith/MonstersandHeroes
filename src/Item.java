public abstract class Item implements Buyable, Sellable {
    private final String name;
    private final double price;
    private final int level;

    public Item(String name, double price, int level) {
        this.name = name;
        this.price = price;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public double getSellPrice() {
        return price / 2;
    }

    public int getLevel() {
        return level;
    }

    public abstract Item copy();
}

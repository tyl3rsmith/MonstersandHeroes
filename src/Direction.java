/**
 * Used to abstract the logic for moving in different directions
 * Position of the party is updated each direction moved
 */

public enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    private final int rowChange;
    private final int colChange;

    Direction(int rx, int cy) {
        this.rowChange = rx;
        this.colChange = cy;
    }

    public int getRowChange() {
        return rowChange;
    }

    public int getColChange() {
        return colChange;
    }
}
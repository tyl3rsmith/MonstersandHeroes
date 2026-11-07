/**
 * Position
 * Mainly used to manage positions across the map for the hero party
 */

public class Position {
    private final int row;
    private final int col;

    public Position(int row, int col) {
        if (row < 0 || col < 0) {
            throw new IllegalArgumentException("invalid row or column");
        }

        this.row = row;
        this.col = col;
    }

    public Position() {
        this(0, 0);
    }

    public int getCol() {
        return this.col;
    }

    public int getRow() {
        return this.row;
    }
}
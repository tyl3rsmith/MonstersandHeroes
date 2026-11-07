import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

/**
 * Represents the main Map
 * 20% of tiles are Inaccessible, 30% of tiles are Markets, 50% are Common where battles can occur
 * Handles the grid state and manages positions of each space
 */

public class Grid {
    // ratios for generating space types randomly
    private static final double INACCESSIBLE_RATIO = 0.2;
    private static final double MARKET_RATIO = 0.3;

    // row and column changes for up, down, left, right
    private static final int[] ROW_CHANGES = {-1, 1, 0, 0};
    private static final int[] COL_CHANGES = {0, 0, -1, 1};

    private final Space[][] grid;
    private final int dim;
    private final Random rand = new Random();

    public Grid(int dim) {
        if (dim <= 0) {
            throw new IllegalArgumentException("grid dimension must be positive");
        }

        this.grid = new Space[dim][dim];
        this.dim = dim;
        initGrid(); // randomly initialize grid
    }

    // keeps generating random layouts until every accessible space can be reached from the start
    // some random generations block heroes in and need to be regenerated
    private void initGrid() {
        int numInaccessibleSpaces = (int) Math.ceil(dim * dim * INACCESSIBLE_RATIO);
        int numMarketSpaces = (int) Math.ceil(dim * dim * MARKET_RATIO);

        boolean connected = false;
        while (!connected) {
            Set<String> used = new HashSet<>();

            initCommonSpaces();
            initInaccessibleSpaces(used, numInaccessibleSpaces);
            initMarketSpaces(used, numMarketSpaces);

            connected = isGridConnected(); // determines connectivity by running bfs
        }
    }

    private void initCommonSpaces() {
        for (int row = 0; row < dim; row++) {
            for (int col = 0; col < dim; col++) {
                grid[row][col] = new Space(SpaceType.COMMON);
            }
        }
    }

    private void initInaccessibleSpaces(Set<String> used, int numInaccessibleSpaces) {
        while (numInaccessibleSpaces > 0) {
            int row = rand.nextInt(dim);
            int col = rand.nextInt(dim);
            String key = row + ", " + col;

            if (!used.contains(key) && !(row == 0 && col == 0)) { // starting space is never altered
                grid[row][col] = new Space(SpaceType.INACCESSIBLE);
                used.add(key);
                numInaccessibleSpaces--;
            }
        }
    }

    private void initMarketSpaces(Set<String> used, int numMarketSpaces) {
        while (numMarketSpaces > 0) {
            int row = rand.nextInt(dim);
            int col = rand.nextInt(dim);
            String key = row + ", " + col;

            if (!used.contains(key) && !(row == 0 && col == 0)) { // starting space is never altered
                grid[row][col] = new Space(SpaceType.MARKET);
                used.add(key);
                numMarketSpaces--;
            }
        }
    }

    // Used to determine if we can reach all accessible spaces from the start space
    // Implemented by running a standard BFS traversal only considering accessible spaces

    private boolean isGridConnected() {
        boolean[][] visited = new boolean[dim][dim];
        Queue<Position> queue = new LinkedList<>();

        queue.add(new Position(0, 0));
        visited[0][0] = true;

        int accessibleSpaces = countAccessibleSpaces();
        int reachableSpaces = 0;

        while (!queue.isEmpty()) {
            Position current = queue.remove();
            reachableSpaces++; // while we can access an accessible space inc the count, eventually should equal total num of accessible spaces

            for (int i = 0; i < ROW_CHANGES.length; i++) {
                int newRow = current.getRow() + ROW_CHANGES[i];
                int newCol = current.getCol() + COL_CHANGES[i];

                if (newRow < 0 || newRow >= dim || newCol < 0 || newCol >= dim) {
                    continue;
                }
                if (visited[newRow][newCol] || !grid[newRow][newCol].getSpaceType().isAccessible()) {
                    continue;
                }

                visited[newRow][newCol] = true;
                queue.add(new Position(newRow, newCol));
            }
        }

        return reachableSpaces == accessibleSpaces;
    }

    // helper method for BFS to know how many spaces it needs to reach
    private int countAccessibleSpaces() {
        int count = 0;
        for (int row = 0; row < dim; row++) {
            for (int col = 0; col < dim; col++) {
                if (grid[row][col].getSpaceType().isAccessible()) {
                    count++;
                }
            }
        }
        return count;
    }

    // validates a position is in bounds of the map
    private boolean isPositionValid(Position position) {
        return position != null
                && position.getRow() >= 0 && position.getRow() < dim
                && position.getCol() >= 0 && position.getCol() < dim;
    }

    public int getDim() {
        return dim;
    }

    public Space getSpace(Position position) {
        if (!isPositionValid(position)) {
            throw new IndexOutOfBoundsException("position outside grid boundaries");
        }
        return grid[position.getRow()][position.getCol()];
    }

    public void setSpace(Position position, Space space) {
        if (!isPositionValid(position)) {
            throw new IndexOutOfBoundsException("position outside grid boundaries");
        }
        if (space == null) {
            throw new IllegalArgumentException("space cannot be null");
        }

        grid[position.getRow()][position.getCol()] = space;
    }

    // methods below handle displaying the grid
    public void displayGrid(Position partyLocation) {
        printColumnNumbers();

        for (int row = 0; row < dim; row++) {
            printRow(row, partyLocation);

            if (row < dim - 1) {
                printSeparator();
            }
        }
    }

    private void printColumnNumbers() {
        System.out.print("  ");
        for (int col = 0; col < dim; col++) {
            System.out.print(col + "   ");
        }
        System.out.println();
    }

    private void printRow(int row, Position partyLocation) {
        System.out.print(row + " ");

        for (int col = 0; col < dim; col++) {
            System.out.print(symbolAt(row, col, partyLocation));

            if (col < dim - 1) {
                System.out.print(" | ");
            }
        }
        System.out.println();
    }

    private void printSeparator() {
        System.out.print("  ");

        for (int col = 0; col < dim; col++) {
            System.out.print("---");

            if (col < dim - 1) {
                System.out.print("+");
            }
        }
        System.out.println();
    }

    // P = party, M = market, X = inaccessible, blank = common
    private char symbolAt(int row, int col, Position partyLocation) {
        if (row == partyLocation.getRow() && col == partyLocation.getCol()) {
            return 'P';
        }

        return switch (grid[row][col].getSpaceType()) {
            case COMMON -> ' ';
            case MARKET -> 'M';
            case INACCESSIBLE -> 'X';
        };
    }
}
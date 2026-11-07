import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.HashSet;

public class Grid {
    private final Space[][] grid;
    private final int dim;
    private final Random rand = new Random();

    public Grid(int dim) {
        if (dim <= 0) {
            throw new IllegalArgumentException("grid dimension must be positive");
        }

        this.grid = new Space[dim][dim];
        this.dim = dim;
        initGrid();

    }

    // initialize each spot on the grid to be a space

    private void initGrid() {
        boolean connected = false;
        int numInaccessibleSpaces = (int) Math.ceil(Math.pow(dim, 2) * 0.2);
        int numMarketSpaces = (int) Math.ceil(Math.pow(dim, 2) * 0.3);

        while (!connected) {
            HashSet<String> used = new HashSet<>();

            initCommonSpaces();
            initInaccessibleSpaces(used, numInaccessibleSpaces);
            initMarketSpaces(used, numMarketSpaces);

            connected = isGridConnected();
        }
    }

    private void initCommonSpaces() {
        for (int i = 0; i < this.dim; i++) {
            for (int j = 0; j < this.dim; j++) {
                this.grid[i][j] = new Space(SpaceType.COMMON);
            }
        }
    }

    private void initInaccessibleSpaces(HashSet<String> used, int numInaccessibleSpaces) {
        while (numInaccessibleSpaces > 0) {
            int randomRow = rand.nextInt(this.dim);
            int randomCol = rand.nextInt(this.dim);
            String key = randomRow + ", " + randomCol;
            if (!used.contains(key) && !(randomRow == 0 && randomCol == 0)) {
                this.grid[randomRow][randomCol] = new Space(SpaceType.INACCESSIBLE);
                numInaccessibleSpaces -= 1;
                used.add(key);
            }
        }
    }

    private void initMarketSpaces(HashSet<String> used, int numMarketSpaces) {
        while (numMarketSpaces > 0) {
            int randomRow = rand.nextInt(this.dim);
            int randomCol = rand.nextInt(this.dim);
            String key = randomRow + ", " + randomCol;
            if (!used.contains(key) && !(randomRow == 0 && randomCol == 0)) {
                this.grid[randomRow][randomCol] = new Space(SpaceType.MARKET);
                numMarketSpaces -= 1;
                used.add(key);
            }
        }
    }

    private boolean isPositionValid(Position position) {
        return position != null && position.getRow() >= 0 && position.getRow() < this.dim && position.getCol() >= 0 && position.getCol() < this.dim;
    }

    public int getDim() {
        return dim;
    }

    public Space getSpace(Position position) {
        if (!isPositionValid(position)) {
            throw new IndexOutOfBoundsException("position outside grid boundaries");
        }
        return this.grid[position.getRow()][position.getCol()];
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

    public void displayGrid(Position partyLocation) {
        System.out.print("  ");

        for (int col = 0; col < dim; col++) {
            System.out.print(col + "   ");
        }

        System.out.println();

        for (int row = 0; row < dim; row++) {
            System.out.print(row + " ");

            for (int col = 0; col < dim; col++) {
                SpaceType type = grid[row][col].getSpaceType();

                if (row == partyLocation.getRow() && col == partyLocation.getCol()) {
                    System.out.print("P");
                } else {
                    switch (type) {
                        case COMMON -> System.out.print(" ");
                        case MARKET -> System.out.print("M");
                        case INACCESSIBLE -> System.out.print("X");
                    }
                }

                if (col < dim - 1) {
                    System.out.print(" | ");
                }
            }

            System.out.println();

            if (row < dim - 1) {
                System.out.print("  ");

                for (int col = 0; col < dim; col++) {
                    System.out.print("---");

                    if (col < dim - 1) {
                        System.out.print("+");
                    }
                }

                System.out.println();
            }
        }
    }

    // BFS helper method to check that the grid isn't trapped by inaccessible spaces
    private boolean isGridConnected() {
        boolean[][] visited = new boolean[dim][dim];
        Queue<Position> queue = new LinkedList<>();

        // Start BFS at the party's starting position
        Position start = new Position(0, 0);
        queue.add(start);
        visited[0][0] = true;

        // Directions: up, down, left, right
        int[] rowChanges = {-1, 1, 0, 0};
        int[] colChanges = {0, 0, -1, 1};

        int reachableSpaces = 0;
        int accessibleSpaces = 0;

        // Count all accessible spaces in the grid
        for (int row = 0; row < dim; row++) {
            for (int col = 0; col < dim; col++) {
                if (grid[row][col].getSpaceType().isAccessible()) {
                    accessibleSpaces++;
                }
            }
        }

        // BFS
        while (!queue.isEmpty()) {
            Position current = queue.remove();

            reachableSpaces++;

            for (int i = 0; i < 4; i++) {
                int newRow = current.getRow() + rowChanges[i];
                int newCol = current.getCol() + colChanges[i];

                // Check grid boundaries
                if (newRow < 0 || newRow >= dim ||
                        newCol < 0 || newCol >= dim) {
                    continue;
                }

                // Skip inaccessible or already visited spaces
                if (!grid[newRow][newCol].getSpaceType().isAccessible() ||
                        visited[newRow][newCol]) {
                    continue;
                }

                visited[newRow][newCol] = true;
                queue.add(new Position(newRow, newCol));
            }
        }

        return reachableSpaces == accessibleSpaces;
    }
}

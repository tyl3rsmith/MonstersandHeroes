/**
 * SpaceType Enum
 * 3 types of spaces: Inaccessible, Market, Common
 * Defines which space can be accessed
 * Useful for defining traversal logic for heroes across the map
 */


public enum SpaceType {
    INACCESSIBLE(false),
    MARKET(true),
    COMMON(true);

    private final boolean accessible;

    SpaceType(boolean accessible) {
        this.accessible = accessible;
    }

    public boolean isAccessible() {
        return accessible;
    }
}
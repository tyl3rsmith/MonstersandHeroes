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
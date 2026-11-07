/**
 * Space
 * Abstracts the type of Space on the Map
 */

public class Space {
    private final SpaceType spaceType;

    public Space(SpaceType spaceType) {
        this.spaceType = spaceType;
    }

    public SpaceType getSpaceType() {
        return this.spaceType;
    }
}
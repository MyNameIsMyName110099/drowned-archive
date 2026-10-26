package drownedarchive.world;

import java.util.Locale;
import java.util.Optional;

/*
 * The four ways the player can swim. Each direction stores how it changes your
 * position in a ruin's 2D array, so the movement math lives in one place.
 * (Started from my Chapter 1 lab version.)
 */
public enum Direction {
    NORTH(-1, 0),
    SOUTH(1, 0),
    EAST(0, 1),
    WEST(0, -1);

    private final int rowChange;
    private final int colChange;

    Direction(int rowChange, int colChange) {
        this.rowChange = rowChange;
        this.colChange = colChange;
    }

    public int getRowChange() {
        return rowChange;
    }

    public int getColChange() {
        return colChange;
    }

    public Direction opposite() {
        switch (this) {
            case NORTH: return SOUTH;
            case SOUTH: return NORTH;
            case EAST:  return WEST;
            case WEST:  return EAST;
            // Only reachable if a new direction is added without updating this switch.
            default:    throw new IllegalStateException("No opposite defined for " + name());
        }
    }

    /*
     * Turns what the player typed ("north", "N", "w") into a Direction.
     * Returns an empty Optional instead of throwing like valueOf() does, because a
     * typo from the player is normal and shouldn't crash anything.
     */
    public static Optional<Direction> fromWord(String word) {
        if (word == null) {
            return Optional.empty();
        }
        String cleaned = word.trim().toLowerCase(Locale.ROOT);
        for (Direction direction : values()) {
            String name = direction.name().toLowerCase(Locale.ROOT);
            if (cleaned.equals(name) || cleaned.equals(name.substring(0, 1))) {
                return Optional.of(direction);
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }
}

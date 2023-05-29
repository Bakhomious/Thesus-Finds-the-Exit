package labyrinth.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a wall in the labyrinth.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Wall {
    private Position position;
    @Getter
    private Direction direction;

    /**
     * Represents the direction of the wall.
     */
    public enum Direction {
        RIGHT, BOTTOM, LEFT, TOP;
    }

    @Override
    public String toString() {
        return String.format("{%s, %s}", position, direction);
    }
}

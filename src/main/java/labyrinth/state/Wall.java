package labyrinth.state;

import javafx.geometry.Pos;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Wall {
    private Position position;
    private Direction direction;

    enum Direction {
        RIGHT, BOTTOM, LEFT, TOP
    }

    @Override
    public String toString() {
        return String.format("{%s, %s}", position, direction);
    }
}

package labyrinth.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoveDirectionTest {

    @Test
    void of() {
        assertSame(MoveDirection.UP, MoveDirection.of(-1, 0));
        assertSame(MoveDirection.RIGHT, MoveDirection.of(0, 1));
        assertSame(MoveDirection.DOWN, MoveDirection.of(1, 0));
        assertSame(MoveDirection.LEFT, MoveDirection.of(0, -1));
    }

    @Test
    void of_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> MoveDirection.of(0, 0));
    }

}

package labyrinth.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ParameterizedWallTest {

    static Stream<Wall> wallProvider() {
        return Stream.of(new Wall(new Position(0, 0), Wall.Direction.RIGHT),
                new Wall(new Position(1, 0), Wall.Direction.BOTTOM),
                new Wall(new Position(3, 6), Wall.Direction.LEFT),
                new Wall(new Position(2, 5), Wall.Direction.TOP));
    }

    @ParameterizedTest
    @MethodSource("wallProvider")
    void testToString(Wall wall) {
        assertEquals(String.format("{%s, %s}", wall.getPosition(), wall.getDirection()), wall.toString());
    }

    @ParameterizedTest
    @MethodSource("wallProvider")
    void testEquals(Wall wall) {
        Wall sameWall = new Wall(wall.getPosition(), wall.getDirection());
        assertTrue(wall.equals(sameWall));
    }

    @ParameterizedTest
    @MethodSource("wallProvider")
    void testHashCode(Wall wall) {
        Wall sameWall = new Wall(wall.getPosition(), wall.getDirection());
        assertEquals(wall.hashCode(), sameWall.hashCode());
    }

}
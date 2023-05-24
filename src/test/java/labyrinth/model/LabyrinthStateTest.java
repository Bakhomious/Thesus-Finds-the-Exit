package labyrinth.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class LabyrinthStateTest {

    private LabyrinthState state1 = new LabyrinthState(); // the original initial state

    private LabyrinthState state2 = new LabyrinthState("/goalstate.json"); // a goal state

    private LabyrinthState state3 = new LabyrinthState("/nongoalstate.json"); // a non-goal state

    private LabyrinthState state4 = new LabyrinthState("/deadendstate.json"); // No possible moves in this state

    @Test
    void constructor_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new LabyrinthState("/invalid/state1.json"));
        assertThrows(IllegalArgumentException.class, () -> new LabyrinthState("/invalid/state2.json"));
        assertThrows(IllegalArgumentException.class, () -> new LabyrinthState("/invalid/state3.json"));
        assertThrows(IllegalArgumentException.class, () -> new LabyrinthState("/invalid/state4.json"));
    }

    @Test
    void isGoal() {
        assertFalse(state1.isGoal());
        assertTrue(state2.isGoal());
        assertFalse(state3.isGoal());
        assertFalse(state4.isGoal());
    }

    @Test
    void goalProperty() {
        assertEquals(state1.isGoal(), state1.goalProperty().get());
        assertEquals(state2.isGoal(), state2.goalProperty().get());
        assertEquals(state3.isGoal(), state3.goalProperty().get());
        assertEquals(state4.isGoal(), state4.goalProperty().get());
    }

    @Test
    void move_up_state1() {
        var expected = state1.wallPositionInDirection(MoveDirection.UP);
        state1.move(MoveDirection.UP);
        assertEquals(expected, state1.getPosition(state1.BLUE_BALL));
    }

    @Test
    void move_down_state1() {
        var expected = state1.wallPositionInDirection(MoveDirection.DOWN);
        state1.move(MoveDirection.DOWN);
        assertEquals(expected, state1.getPosition(state1.BLUE_BALL));
    }

    @Test
    void move_left_state1() {
        var expected = state1.wallPositionInDirection(MoveDirection.LEFT);
        state1.move(MoveDirection.LEFT);
        assertEquals(expected, state1.getPosition(state1.BLUE_BALL));
    }

    @Test
    void move_right_state1() {
        var expected = state1.wallPositionInDirection(MoveDirection.RIGHT);
        state1.move(MoveDirection.RIGHT);
        assertEquals(expected, state1.getPosition(state1.BLUE_BALL));
    }

    @Test
    void canMove_state1() {
        assertTrue(state1.canMove(MoveDirection.UP, state1.getPosition(state1.BLUE_BALL)));
        assertTrue(state1.canMove(MoveDirection.DOWN, state1.getPosition(state1.BLUE_BALL)));
        assertTrue(state1.canMove(MoveDirection.LEFT, state1.getPosition(state1.BLUE_BALL)));
        assertTrue(state1.canMove(MoveDirection.RIGHT, state1.getPosition(state1.BLUE_BALL)));
    }

    @Test
    void canMove_state2() {
        assertFalse(state2.canMove(MoveDirection.DOWN, state2.getPosition(state2.BLUE_BALL)));
        assertFalse(state2.canMove(MoveDirection.RIGHT, state2.getPosition(state2.BLUE_BALL)));
        assertFalse(state2.canMove(MoveDirection.LEFT, state2.getPosition(state2.BLUE_BALL)));
        assertTrue(state2.canMove(MoveDirection.UP, state2.getPosition(state2.BLUE_BALL)));
    }

    @Test
    void canMove_state3() {
        assertFalse(state3.canMove(MoveDirection.UP, state3.getPosition(state3.BLUE_BALL)));
        assertFalse(state3.canMove(MoveDirection.RIGHT, state3.getPosition(state3.BLUE_BALL)));
        assertFalse(state3.canMove(MoveDirection.LEFT, state3.getPosition(state3.BLUE_BALL)));
        assertTrue(state3.canMove(MoveDirection.DOWN, state3.getPosition(state3.BLUE_BALL)));
    }

    @Test
    void canMove_state4() {
        assertFalse(state4.canMove(MoveDirection.DOWN, state4.getPosition(state4.BLUE_BALL)));
        assertFalse(state4.canMove(MoveDirection.RIGHT, state4.getPosition(state4.BLUE_BALL)));
        assertFalse(state4.canMove(MoveDirection.LEFT, state4.getPosition(state4.BLUE_BALL)));
        assertFalse(state4.canMove(MoveDirection.UP, state4.getPosition(state4.BLUE_BALL)));
    }

    @Test
    void getWallDirectionsAtPosition() {
        var wallDirections1 = new HashSet<Wall.Direction>();
        assertEquals(state1.getWallDirectionsAtPosition(state1.getPosition(state1.BLUE_BALL)), wallDirections1);

        var wallDirections2 = new HashSet<Wall.Direction>();
        wallDirections2.add(Wall.Direction.BOTTOM);
        wallDirections2.add(Wall.Direction.RIGHT);
        wallDirections2.add(Wall.Direction.LEFT);
        assertEquals(state2.getWallDirectionsAtPosition(state2.getPosition(state2.BLUE_BALL)), wallDirections2);
    }

}
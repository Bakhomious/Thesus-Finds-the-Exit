package labyrinth.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ParameterizedLabyrinthStateTest {

    static Stream<LabyrinthState> stateProvider() {
        return Stream.of(
                new LabyrinthState(),
                new LabyrinthState("/goalstate.json"),
                new LabyrinthState("/nongoalstate.json"),
                new LabyrinthState("/deadendstate.json")
        );
    }

    @ParameterizedTest
    @MethodSource("stateProvider")
    void goalProperty(LabyrinthState state) {
        assertEquals(state.isGoal(), state.goalProperty().get());
    }

    @ParameterizedTest
    @MethodSource("stateProvider")
    void move_up(LabyrinthState state) {
        var expected = state.wallPositionInDirection(MoveDirection.UP);
        state.move(MoveDirection.UP);
        assertEquals(expected, state.getPosition(state.BLUE_BALL));
    }

    @ParameterizedTest
    @MethodSource("stateProvider")
    void move_down(LabyrinthState state) {
        var expected = state.wallPositionInDirection(MoveDirection.DOWN);
        state.move(MoveDirection.DOWN);
        assertEquals(expected, state.getPosition(state.BLUE_BALL));
    }

    @ParameterizedTest
    @MethodSource("stateProvider")
    void move_left(LabyrinthState state) {
        var expected = state.wallPositionInDirection(MoveDirection.LEFT);
        state.move(MoveDirection.LEFT);
        assertEquals(expected, state.getPosition(state.BLUE_BALL));
    }

    @ParameterizedTest
    @MethodSource("stateProvider")
    void move_right(LabyrinthState state) {
        var expected = state.wallPositionInDirection(MoveDirection.RIGHT);
        state.move(MoveDirection.RIGHT);
        assertEquals(expected, state.getPosition(state.BLUE_BALL));
    }


    @ParameterizedTest
    @MethodSource("stateProvider")
    void positionProperty(LabyrinthState state) {
        assertEquals(state.getPosition(state.BLUE_BALL), state.positionProperty(state.BLUE_BALL).get());
    }

    @ParameterizedTest
    @MethodSource("stateProvider")
    void toStringTest(LabyrinthState state) {
        String toString = state.toString();
        assertTrue(toString.contains("Blue Ball: " + state.getPosition(state.BLUE_BALL)));
        assertTrue(toString.contains("Goal: " + state.getPosition(state.GOAL_POSITION)));
    }
}
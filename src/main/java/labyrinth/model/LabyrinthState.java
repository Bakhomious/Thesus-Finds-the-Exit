package labyrinth.model;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Represents the state of the labyrinth.
 */
public class LabyrinthState {
    @Getter
    private String path;
    @Getter
    private final int boardSize;
    @Getter
    private final Set<Wall> walls;

    /**
     * The index of the blue ball.
     */
    public static final int BLUE_BALL = 0;
    /**
     * The index of the goal position.
     */
    public static final int GOAL_POSITION = 1;
    private ReadOnlyObjectWrapper<Position>[] positions = new ReadOnlyObjectWrapper[2];
    private ReadOnlyBooleanWrapper goal = new ReadOnlyBooleanWrapper();

    /**
     * Creates a {@code LabyrinthState} object that corresponds to the default state.
     */
    public LabyrinthState() {
        this("/labyrinth.json");
    }

    /**
     * Creates a {@code LabyrinthState} object that corresponds to a configuration.
     * @param path the path of the configuration file
     */
    public LabyrinthState(String path) {
        this.path = path;
        LabyrinthState state = LabyrinthStateLoader.loadFromJson(path);

        this.boardSize = state.boardSize;
        this.walls = state.walls;
        this.positions = state.positions;

        checkConfig();
        goal.bind(positions[BLUE_BALL].isEqualTo(positions[GOAL_POSITION]));
    }

    /**
     * Creates a {@code LabyrinthState} object that corresponds to a configuration.
     * @param boardSize the size of the board
     * @param walls the walls of the board
     * @param positions the positions of the blue ball and the goal
     */
    public LabyrinthState(int boardSize, Set<Wall> walls, ReadOnlyObjectWrapper<Position>[] positions) {
        this.boardSize = boardSize;
        this.walls = walls;
        this.positions = positions;
    }

    private void checkConfig() {
        for(Wall wall : walls) {
            if (!isOnBoard(wall.getPosition())) {
                throw new IllegalArgumentException();
            }
        }
        if (!isOnBoard(getPosition(BLUE_BALL))
                || !isOnBoard(getPosition(GOAL_POSITION))) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * {@return whether the puzzle is solved}
     */
    public boolean isGoal() {
        return goal.get();
    }

    /**
     * {@return goal ReadOnlyProperty}
     */
    public ReadOnlyBooleanProperty goalProperty() {
        return goal.getReadOnlyProperty();
    }

    /**
     * Moves the blue ball in the specified direction.
     * @param moveDirection the direction to which the blue ball is moved
     */
    public void move(MoveDirection moveDirection) {
        Position toPosition = hitWall(moveDirection);
        moveBall(toPosition);
    }

    private Position hitWall(MoveDirection moveDirection) {
        int toRow = getPosition(BLUE_BALL).row();
        int toCol = getPosition(BLUE_BALL).col();

        while(canMove(moveDirection, new Position(toRow, toCol))) {
            switch (moveDirection) {
                case UP -> toRow--;
                case RIGHT -> toCol++;
                case DOWN -> toRow++;
                case LEFT -> toCol--;
            }
        }
        return new Position(toRow, toCol);
    }

    /**
     * {@return the position of the wall in the specified direction}
     * @param moveDirection the direction to which the blue ball is moved
     */
    public Position wallPositionInDirection(MoveDirection moveDirection) {
        return hitWall(moveDirection);
    }

    /**
     * {@return whether the blue ball is able to move in the specified direction from the specified position}
     * @param moveDirection the direction to which the blue ball is moved
     * @param position the position of the blue ball
     */
    public boolean canMove(MoveDirection moveDirection, Position position) {
        return switch (moveDirection) {
            case UP -> canMoveUp(position);
            case RIGHT -> canMoveRight(position);
            case DOWN -> canMoveDown(position);
            case LEFT -> canMoveLeft(position);
        };
    }

    private boolean canMoveUp(Position position) {
        if(position.row() > 0) {
            if (isWall(new Wall(new Position(position.row(), position.col()), Wall.Direction.TOP))) {
                return false;
            }
            return !isWall(new Wall(position.getUp(), Wall.Direction.BOTTOM));
        }
        return false;
    }

    private boolean canMoveRight(Position position) {
        if(position.col() < boardSize - 1) {
            if (isWall(new Wall(new Position(position.row(), position.col()), Wall.Direction.RIGHT))) {
                return false;
            }
            return !isWall(new Wall(position.getRight(), Wall.Direction.LEFT));
        }
        return false;
    }

    private boolean canMoveDown(Position position) {
        if(position.row() < boardSize - 1) {
            if (isWall(new Wall(new Position(position.row(), position.col()), Wall.Direction.BOTTOM))) {
                return false;
            }
            return !isWall(new Wall(position.getDown(), Wall.Direction.TOP));
        }
        return false;
    }

    private boolean canMoveLeft(Position position) {
        if(position.col() > 0) {
            if (isWall(new Wall(new Position(position.row(), position.col()), Wall.Direction.LEFT))) {
                return false;
            }
            return !isWall(new Wall(position.getLeft(), Wall.Direction.RIGHT));
        }
        return false;
    }

    private boolean isWall(Wall testWall) {
        return walls.contains(testWall);
    }

    /**
     * {@return the set of directions in which there is a wall at the specified position}
     * @param position the position of the blue ball
     */
    public Set<Wall.Direction> getWallDirectionsAtPosition(Position position) {
        Set<Wall.Direction> wallDirections = new HashSet<>();
        for(var wallDirection : Wall.Direction.values())
        {
            if(isWall(new Wall(position, wallDirection))) {
                wallDirections.add(wallDirection);
            }
        }
        return wallDirections;
    }

    private boolean isOnBoard(Position position) {
        return position.row() >= 0 && position.row() < boardSize
                && position.col() >= 0 && position.col() < boardSize;
    }


    private void moveBall(Position toPosition) {
        positions[BLUE_BALL].set(toPosition);
    }

    /**
     * {@return the position of the given index}
     * @param n the index of the position
     */
    public Position getPosition(int n) {
        return positions[n].get();
    }

    /**
     * {@return the ReadOnlyProperty of the given index}
     * @param n the index of the position
     */
    public ReadOnlyObjectProperty<Position> positionProperty(int n) {
        return positions[n].getReadOnlyProperty();
    }

    @Override
    public String toString() {
        var sj = new StringJoiner("; ", "[", "]");
        walls.forEach(wall -> sj.add(wall.toString()));
        sj.add(String.format("\nBlue Ball: %s, Goal: %s",
                getPosition(BLUE_BALL).toString(), getPosition(GOAL_POSITION).toString()));
        return sj.toString();
    }

    public static void main(String[] args) {
        LabyrinthState labyrinthState = new LabyrinthState();
        System.out.println(labyrinthState);
    }

}

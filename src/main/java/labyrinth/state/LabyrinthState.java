package labyrinth.state;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

@AllArgsConstructor
public class LabyrinthState {

    /**
     * The size of the board.
     */
    private int boardSize;
    private Set<Wall> walls = new HashSet<>();
    private Position blueBall;
    private Position goalPosition;

    public LabyrinthState() {
        LabyrinthState state = loadLabyrinthState("/labyrinth.json");
        if (state == null) {
            throw new IllegalStateException();
        }

        this.boardSize = state.boardSize;
        this.walls = state.walls;
        this.blueBall = state.blueBall;
        this.goalPosition = state.goalPosition;

        checkConfig();

    }

    private LabyrinthState loadLabyrinthState(String path) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = LabyrinthState.class.getResourceAsStream(path);
            JsonNode jsonNode = mapper.readTree(inputStream);

            int boardSize = mapper.treeToValue(jsonNode.get("boardSize"), Integer.class);
            Position blueBallPosition = mapper.treeToValue(jsonNode.get("blueBall"), Position.class);
            Position goalPosition = mapper.treeToValue(jsonNode.get("goalPosition"), Position.class);
            Wall[] walls = mapper.treeToValue(jsonNode.get("walls"), Wall[].class);
            Set<Wall> wallSet = processWalls(walls);

            return new LabyrinthState(boardSize, wallSet, blueBallPosition, goalPosition);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Set<Wall> processWalls(Wall... walls) {
        Set<Wall> wallSet = new HashSet<>();

        for(Wall wall: walls) {
            wallSet.add(wall);
            switch (wall.getDirection()) {
                case RIGHT ->
                        wallSet.add(new Wall(wall.getPosition()
                                .getPosition(MoveDirection.RIGHT),
                                Wall.Direction.LEFT));
                case BOTTOM ->
                        wallSet.add(new Wall(wall.getPosition()
                                .getPosition(MoveDirection.DOWN),
                                Wall.Direction.TOP));
            }
        }
        return wallSet;
    }

    private void checkConfig() {
        for(Wall wall : walls) {
            if (!isOnBoard(wall.getPosition())) {
                throw new IllegalStateException();
            }
        }
        if (!isOnBoard(blueBall)
                || !isOnBoard(goalPosition)) {
            throw new IllegalStateException();
        }
    }

    // TODO: Move Functions, isGoal, isWall, validMove

    public boolean isGoal() {
        return blueBall.equals(goalPosition);
    }

    public void move(MoveDirection moveDirection) {
        switch (moveDirection) {
            case UP -> moveUp();
            case RIGHT -> moveRight();
            case DOWN -> moveDown();
            case LEFT -> moveLeft();
        }
    }

    private void moveUp() {
        Position toPosition = hitWall(MoveDirection.UP);
        moveBall(toPosition);
    }

    private void moveRight() {
        Position toPosition = hitWall(MoveDirection.RIGHT);
        moveBall(toPosition);
    }

    private void moveDown() {
        Position toPosition = hitWall(MoveDirection.DOWN);
        moveBall(toPosition);
    }

    private void moveLeft() {
        Position toPosition = hitWall(MoveDirection.LEFT);
        moveBall(toPosition);
    }

    private Position hitWall(MoveDirection moveDirection) {
        int toRow = blueBall.row();
        int toCol = blueBall.col();

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

    private boolean isOnBoard(Position position) {
        return position.row() >= 0 && position.row() < boardSize
                && position.col() >= 0 && position.col() < boardSize;
    }


    private void moveBall(Position toPosition) {
        blueBall = toPosition;
    }

    @Override
    public String toString() {
        var sj= new StringJoiner(", ", "[", "]");
        walls.forEach(wall -> sj.add(wall.toString()));
        sj.add(String.format("\nBlue Ball: %s, Goal: %s",
                blueBall.toString(), goalPosition.toString()));
        return sj.toString();
    }

    public static void main(String[] args) {
        LabyrinthState labyrinthState = new LabyrinthState();
        System.out.println(labyrinthState);
        labyrinthState.move(MoveDirection.DOWN);
        System.out.println(labyrinthState);
        labyrinthState.move(MoveDirection.RIGHT);
        System.out.println(labyrinthState);
        labyrinthState.move(MoveDirection.LEFT);
        System.out.println(labyrinthState);
        labyrinthState.move(MoveDirection.UP);
        System.out.println(labyrinthState);
    }

}

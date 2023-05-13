package labyrinth.state;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

public class LabyrinthState {

    /**
     * The size of the board.
     */
    public static final int BOARD_SIZE = 7;

    private Set<Wall> walls = new HashSet<>();
    private Position blueBallPosition;
    private Position goalPosition;

    public LabyrinthState() {
        this(loadWalls("/labyrinth.json"));
        // TODO: create a JSON file for the initial state
        blueBallPosition = new Position(1, 4);
        goalPosition = new Position(5, 2);
    }

    public LabyrinthState(Set<Wall> walls) {
        this.walls = walls;
    }

    // TODO: Refactor Function
    private static Set<Wall> loadWalls(String path) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = LabyrinthState.class.getResourceAsStream(path);
            Wall[] walls = mapper.readValue(inputStream, Wall[].class);
            Set<Wall> wallSet = new HashSet<>();

            for (Wall wall: walls) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // TODO: Move Functions, isGoal, isWall, validMove

    @Override
    public String toString() {
        var sj= new StringJoiner(", ", "[", "]");
        walls.forEach(wall -> sj.add(wall.toString()));
        return sj.toString();
    }

    public static void main(String[] args) {
        LabyrinthState labyrinthState = new LabyrinthState();
        System.out.println(labyrinthState);
    }

}

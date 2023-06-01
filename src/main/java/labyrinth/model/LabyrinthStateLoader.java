package labyrinth.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper class provides methods to load labyrinth configuration
 */
public class LabyrinthStateLoader {

    /**
     * Loads a {@code LabyrinthState} object from a JSON file.
     * @param path the path of the JSON file
     * @return the {@code LabyrinthState} object
     */
    public static LabyrinthState loadFromJson(String path) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = LabyrinthState.class.getResourceAsStream(path);
            JsonNode jsonNode = mapper.readTree(inputStream);

            int boardSize = mapper.treeToValue(jsonNode.get("boardSize"), Integer.class);
            Position blueBallPosition = mapper.treeToValue(jsonNode.get("blueBall"), Position.class);
            Position goalPosition = mapper.treeToValue(jsonNode.get("goalPosition"), Position.class);
            Wall[] walls = mapper.treeToValue(jsonNode.get("walls"), Wall[].class);
            Set<Wall> wallSet = processWalls(walls);

            ReadOnlyObjectWrapper<Position>[] positions = new ReadOnlyObjectWrapper[2];

            positions[LabyrinthState.BLUE_BALL] = new ReadOnlyObjectWrapper<>(blueBallPosition);
            positions[LabyrinthState.GOAL_POSITION] = new ReadOnlyObjectWrapper<>(goalPosition);

            return new LabyrinthState(boardSize, wallSet, positions);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Set<Wall> processWalls(Wall... walls) {
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
}

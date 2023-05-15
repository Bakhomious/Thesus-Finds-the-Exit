package labyrinth.gui;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import labyrinth.model.LabyrinthState;
import labyrinth.model.MoveDirection;
import labyrinth.model.Position;
import labyrinth.util.ImageStorage;
import labyrinth.util.OrdinalImageStorage;
import org.tinylog.Logger;

import java.util.Optional;

public class GameController {

    @FXML
    private GridPane grid;
    @FXML
    private TextField numberOfMovesField;

    private ImageStorage<Integer> imageStorage = new OrdinalImageStorage("/labyrinth/model",
            "ball.png",
            "goal.png");
    private LabyrinthState state;

    private IntegerProperty numberOfMoves = new SimpleIntegerProperty(0);

    @FXML
    private void initialize() {
        createControlBindings();
        restartGame();
        populateGrid();
        registerKeyEventHandler();
    }

    private void createControlBindings() {
        numberOfMovesField.textProperty().bind(numberOfMoves.asString());
    }

    private void restartGame() {
        state = new LabyrinthState();
        numberOfMoves.set(0);
        populateGrid();
        state.goalProperty().addListener(this::handleGameOver);
    }

    private void registerKeyEventHandler() {
        Platform.runLater(() -> grid.getScene().setOnKeyPressed(this::handleKeyPress));
    }

    @FXML
    private void handleMouseClick(MouseEvent event) {
        var source = (Node) event.getSource();
        var row = GridPane.getRowIndex(source);
        var col = GridPane.getColumnIndex(source);
        Logger.debug("Click on square ({},{})", row, col);
        var direction = getDirectionFromClick(row, col);
        direction.ifPresentOrElse(this::performMove,
                () -> Logger.warn("Click does not correspond to any direction"));
    }
    @FXML
    private void handleKeyPress(KeyEvent keyEvent) {
        var restartKeyCombination = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
        var quitKeyCombination = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
        if (restartKeyCombination.match(keyEvent)) {
            Logger.debug("Restarting game");
            restartGame();
        } else if (quitKeyCombination.match(keyEvent)) {
            Logger.debug("Quitting game");
            Platform.exit();
        } else if (keyEvent.getCode() == KeyCode.UP) {
            Logger.debug("UP pressed");
            performMove(MoveDirection.UP);
        } else if (keyEvent.getCode() == KeyCode.RIGHT) {
            Logger.debug("RIGHT pressed");
            performMove(MoveDirection.RIGHT);
        } else if (keyEvent.getCode() == KeyCode.DOWN) {
            Logger.debug("DOWN pressed");
            performMove(MoveDirection.DOWN);
        } else if (keyEvent.getCode() == KeyCode.LEFT) {
            Logger.debug("LEFT pressed");
            performMove(MoveDirection.LEFT);
        }
    }

    private void performMove(MoveDirection moveDirection) {
        if (state.canMove(moveDirection, state.getBlueBall().get())) {
            Logger.info("Moving {}", moveDirection);
            state.move(moveDirection);
            Logger.trace("New state: {}", state);
            numberOfMoves.set(numberOfMoves.get() + 1);
        } else {
            Logger.warn("Invalid move: {}", moveDirection);
        }
    }

    private void handleGameOver(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
        if(newValue) {
            var alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Game Over");
            alert.setContentText("Congratulations, you have solved the puzzle!");
            alert.showAndWait();
        }
    }

    private void populateGrid() {
        for (var row = 0; row < grid.getRowCount(); row++) {
            for (var col = 0; col < grid.getColumnCount(); col++) {
                var square = createSquare(row, col);
                grid.add(square, col, row);
            }
        }
    }

    private StackPane createSquare(int row, int col) {
        var square = new StackPane();
        square.getStyleClass().add("square");
        var wallDirections = state.getWallDirectionsAtPosition(new Position(row, col));
        if(!wallDirections.isEmpty()) {
            for(var wallDirection : wallDirections) {
                String wallClass = String.valueOf(wallDirection).toLowerCase() + "Wall";
                square.getStyleClass().add(wallClass);
            }
        }

        var ballView = new ImageView(imageStorage.get(0));
        ballView.setFitWidth(75);
        ballView.setFitHeight(75);
        ballView.visibleProperty().bind(createBindingForPieceAtPosition(row, col));
        square.getChildren().add(ballView);

        if(state.getGoalPosition().equals(new Position(row, col))) {
            var goalView = new ImageView(imageStorage.get(1));
            goalView.setFitWidth(75);
            goalView.setFitHeight(75);
            square.getChildren().add(goalView);
        }
        square.setOnMouseClicked(this::handleMouseClick);
        return square;
    }

    private BooleanBinding createBindingForPieceAtPosition(int row, int col) {
        return new BooleanBinding() {
            {
                super.bind(state.getBlueBall());
            }
            @Override
            protected boolean computeValue() {
                var pos = state.getBlueBall().get();
                return pos.row() == row && pos.col() == col;
            }
        };
    }

    private Optional<MoveDirection> getDirectionFromClick(int row, int col) {
        var ballPos = state.getBlueBall();
        MoveDirection direction = null;
        try {
            direction = MoveDirection.of(row - ballPos.get().row(), col - ballPos.get().col());
        } catch (IllegalArgumentException e) {
        }
        return Optional.ofNullable(direction);
    }


}

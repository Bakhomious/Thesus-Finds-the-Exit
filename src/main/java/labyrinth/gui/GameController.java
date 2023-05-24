package labyrinth.gui;

import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import labyrinth.model.LabyrinthState;
import labyrinth.model.MoveDirection;
import labyrinth.model.Position;
import labyrinth.results.GameResult;
import labyrinth.results.GameResultRepository;
import labyrinth.util.ControllerHelper;
import labyrinth.util.ImageStorage;
import labyrinth.util.OrdinalImageStorage;
import labyrinth.util.Stopwatch;
import lombok.NonNull;
import lombok.Setter;
import org.tinylog.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class GameController {

    @FXML
    private GridPane grid;
    @FXML
    private TextField numberOfMovesField;
    @FXML
    private Button giveupFinishButton;
    @FXML
    private Button resetButton;

    @FXML
    private Label stopwatchLabel;

    @FXML
    private Label playerGreeting;

    @Setter
    private String playerName;

    private GameResultRepository gameResultRepository = GameResultRepository.getInstance();

    private Stopwatch stopwatch = new Stopwatch();

    private Instant startTime;

    private ImageStorage<Integer> imageStorage = new OrdinalImageStorage("/images",
            "ball.png",
            "goal.png");
    private LabyrinthState state;

    private IntegerProperty numberOfMoves = new SimpleIntegerProperty(0);
    private FXMLLoader fxmlLoader = new FXMLLoader();
    private boolean isSolved;

    @FXML
    private void initialize() {
        isSolved = false;
        createControlBindings();
        registerKeyEventHandler();
        restartGame();
    }

    private void createControlBindings() {
        numberOfMovesField.textProperty().bind(numberOfMoves.asString());
        stopwatchLabel.textProperty().bind(stopwatch.hhmmssProperty());
    }

    private void restartGame() {
        state = new LabyrinthState();
        state.goalProperty().addListener(this::handleGameOver);
        numberOfMoves.set(0);

        startTime = Instant.now();
        if (stopwatch.getStatus() == Animation.Status.PAUSED) {
            stopwatch.reset();
        }
        stopwatch.start();
        clearGrid();
        populateGrid();
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
    private void registerKeyEventHandler() {
        final KeyCombination restartKeyCombination = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
        final KeyCombination quitKeyCombination = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
        Platform.runLater(() -> grid.getScene().setOnKeyPressed(
                keyEvent -> {
                    if (isSolved) {
                        return;
                    }
                    if (restartKeyCombination.match(keyEvent)) {
                        Logger.debug("Restarting game...");
                        restartGame();
                    } else if (quitKeyCombination.match(keyEvent)) {
                        Logger.debug("Exiting...");
                        Platform.exit();
                    } else if (keyEvent.getCode() == KeyCode.UP) {
                        Logger.debug("Up arrow pressed");
                        performMove(MoveDirection.UP);
                    } else if (keyEvent.getCode() == KeyCode.RIGHT) {
                        Logger.debug("Right arrow pressed");
                        performMove(MoveDirection.RIGHT);
                    } else if (keyEvent.getCode() == KeyCode.DOWN) {
                        Logger.debug("Down arrow pressed");
                        performMove(MoveDirection.DOWN);
                    } else if (keyEvent.getCode() == KeyCode.LEFT) {
                        Logger.debug("Left arrow pressed");
                        performMove(MoveDirection.LEFT);
                    }
                }
        ));
    }

    private void performMove(MoveDirection moveDirection) {
        if (state.canMove(moveDirection, state.getPosition(state.BLUE_BALL))) {
            Logger.info("Moving {}", moveDirection);
            state.move(moveDirection);
            Logger.trace("New state: {}", state);
            numberOfMoves.set(numberOfMoves.get() + 1);
        } else {
            Logger.warn("Invalid move: {}", moveDirection);
        }
    }

    private void handleGameOver(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
        Platform.runLater(() -> {
            if (newValue) {
                Logger.info("{} has solved the game in {} steps", playerName,numberOfMoves.get());
                playerGreeting.setText("Congratulations!");
                stopwatch.stop();
                resetButton.setDisable(true);
                isSolved = true;
                giveupFinishButton.setText("Finish");
            }
        });
    }

    public void handleGiveUpFinishButton(
            @NonNull final ActionEvent actionEvent) throws IOException {

        final var buttonText = ((Button) actionEvent.getSource()).getText();
        Logger.debug("{} is pressed", buttonText);
        if (Objects.equals(buttonText, "Give Up")) {
            stopwatch.stop();
            Logger.info("The game has been given up");
        }

        Logger.debug("Saving result");
        storeResult();

        Logger.debug("Loading HighScoreController");
        ControllerHelper.loadAndShowFXML(
                fxmlLoader,
                "/fxml/high-scores.fxml",
                (Stage) ((Node) actionEvent.getSource()).getScene().getWindow()
        );
    }

    private void storeResult(){
        Logger.info("Storing game results for player {}", playerName);

        var file = new File("results.json");
        try {
            gameResultRepository.loadFromFile(file);
        } catch (FileNotFoundException e) {
            Logger.warn("File {} was not found, creating one!", file);
        } catch (IOException e){
            Logger.warn("Error reading file {}!", file);
        }
        gameResultRepository.addOne(createGameResult());
        try {
            gameResultRepository.saveToFile(file);
        } catch (IOException e){
            Logger.warn("Error writing file {}!", file);
        }
    }

    public void handleResetButton(ActionEvent actionEvent) {
        Logger.debug("{} is pressed", ((Button) actionEvent.getSource()).getText());
        Logger.info("Resetting game");
        stopwatch.stop();
        restartGame();
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
        for(var i = 0; i < 2; i++) {
            var pieceView = new ImageView(imageStorage.get(i));
            pieceView.setFitWidth(75);
            pieceView.setFitHeight(75);
            pieceView.visibleProperty().bind(createBindingForPieceAtPosition(i, row, col));
            square.getChildren().add(pieceView);
        }
        square.setOnMouseClicked(this::handleMouseClick);
        return square;
    }

    private BooleanBinding createBindingForPieceAtPosition(int n, int row, int col) {
        return new BooleanBinding() {
            {
                super.bind(state.positionProperty(n));
            }
            @Override
            protected boolean computeValue() {
                var pos = state.positionProperty(n).get();
                return pos.row() == row && pos.col() == col;
            }
        };
    }

    private void clearGrid() {
        grid.getChildren().clear();
    }

    private Optional<MoveDirection> getDirectionFromClick(int row, int col) {
        var ballPos = state.getPosition(state.BLUE_BALL);
        MoveDirection direction = null;
        try {
            direction = MoveDirection.of(row - ballPos.row(), col - ballPos.col());
        } catch (IllegalArgumentException e) {
        }
        return Optional.ofNullable(direction);
    }

    private GameResult createGameResult() {
        return GameResult.builder()
                .player(playerName)
                .solved(state.isGoal())
                .duration(Duration.between(startTime, Instant.now()))
                .steps(numberOfMoves.get())
                .build();
    }

}

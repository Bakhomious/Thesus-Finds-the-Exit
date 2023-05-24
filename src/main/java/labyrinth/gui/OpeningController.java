package labyrinth.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import labyrinth.results.GameResultRepository;
import lombok.NonNull;
import org.tinylog.Logger;

import java.io.IOException;

public final class OpeningController {

    private GameResultRepository gameResultRepository;
    @FXML
    private TextField playerNameTextField;
    @FXML
    private Label errorLabel;

    private final FXMLLoader fxmlLoader = new FXMLLoader();


    public void startAction(
            @NonNull final ActionEvent actionEvent) throws IOException {

        if (playerNameTextField.getText().isEmpty()) {
            errorLabel.setText("Please enter your name!");
            errorLabel.setTextFill(Color.RED);
        } else {
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            fxmlLoader.setLocation(getClass().getResource("/fxml/game.fxml"));
            Parent root = fxmlLoader.load();
            fxmlLoader.<GameController>getController().setPlayerName(playerNameTextField.getText());
            stage.setScene(new Scene(root));
            stage.show();
            Logger.info("The user's name is set to {}, loading game scene", playerNameTextField.getText());
        }
    }

}

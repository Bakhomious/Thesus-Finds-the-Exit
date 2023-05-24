package labyrinth.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.tinylog.Logger;

public class GameApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Logger.info("Starting application");
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/opening.fxml"));
        stage.setTitle("Thesus Find the Exit");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }
}

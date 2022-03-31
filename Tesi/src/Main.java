import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class Main extends Application{

    public static final int ALTEZZA = 30;
    public static final int LARGHEZZA = 30;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Pathfinding map");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view.fxml"));
        AnchorPane pane = (AnchorPane) loader.load();
        Scene scene = new Scene(pane);
        Controller controller = (Controller) loader.getController();
        controller.setMain(this);
        controller.setEvents(stage);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}

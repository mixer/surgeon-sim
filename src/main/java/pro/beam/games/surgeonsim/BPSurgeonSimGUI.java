package pro.beam.games.surgeonsim;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class BPSurgeonSimGUI extends Application {
    public static URL loginView = BPSurgeonSimGUI.class.getResource("scenes/login.fxml");
    public static URL runningView = BPSurgeonSimGUI.class.getResource("scenes/running.fxml");
    public static URL loadingView = BPSurgeonSimGUI.class.getResource("scenes/loading.fxml");

    public static void main(String[] args) {
        launch(args);
    }

    public static void changeScene(Stage stage, URL sceneUrl, int width, int height) {
        Parent root;
        try {
            root = FXMLLoader.load(sceneUrl);
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(loginView);

        Scene scene = new Scene(root, 300, 320);

        stage.setTitle("Beam - Surgeon Simulator");
        stage.getIcons().add(new Image(BPSurgeonSimGUI.class.getResourceAsStream("images/beam_ball_color.png")));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest(event -> System.exit(0));
    }
}

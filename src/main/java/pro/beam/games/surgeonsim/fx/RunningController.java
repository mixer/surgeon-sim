package pro.beam.games.surgeonsim.fx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.http.client.HttpResponseException;
import pro.beam.api.exceptions.user.WrongPasswordException;
import pro.beam.games.surgeonsim.BPSurgeonSim;
import pro.beam.games.surgeonsim.StatusListener;

import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class RunningController implements Initializable {
    @FXML
    private Button toggleControls;
    @FXML
    private Label runningText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BPSurgeonSim.getStatusListener().addObserver((Observable o, Object arg) -> this.toggle());
    }

    @FXML
    private void handleToggleControls(ActionEvent event) {
        BPSurgeonSim.getStatusListener().toggleActive();
    }

    private void toggle() {
        Platform.runLater(() -> {
            if (BPSurgeonSim.getStatusListener().getActive()) {
                runningText.setText("Running, controls enabled");
                toggleControls.setText("DISABLE CONTROLS (F7)");
                toggleControls.getStyleClass().remove("enabled");
                toggleControls.getStyleClass().add("disabled");
            } else {
                runningText.setText("Running, controls disabled");
                toggleControls.getStyleClass().remove("disabled");
                toggleControls.getStyleClass().add("enabled");
                toggleControls.setText("ENABLE CONTROLS (F7)");
            }
        });
    }
}

package pro.beam.games.surgeonsim.fx;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.http.client.HttpResponseException;
import pro.beam.api.exceptions.user.WrongPasswordException;
import pro.beam.games.surgeonsim.BPSurgeonSim;
import pro.beam.games.surgeonsim.BPSurgeonSimGUI;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class LoginController implements Initializable {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Button login;
    @FXML
    private Label error;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerMaterialLabel(username, usernameLabel);
        registerMaterialLabel(password, passwordLabel);
    }

    private void registerMaterialLabel(TextField field, Label label) {
        field.focusedProperty().addListener((ObservableValue<? extends Boolean> arg, Boolean oldFocused, Boolean newFocused) -> {
            if (newFocused) {
                if (!label.getStyleClass().contains("focused")) {
                    label.getStyleClass().add("focused");
                }
            } else {
                if (field.getText().isEmpty()) {
                    label.getStyleClass().remove("focused");
                }
            }
        });
    }

    @FXML
    private void handleLoginAction(ActionEvent event) {

        System.out.println("Attempting login");
        error.setText("");
        if (username.getText().isEmpty() || password.getText().isEmpty()) {
            error.setText("Username and password are required.");
            return;
        }

        Stage stage = (Stage) login.getScene().getWindow();
        BPSurgeonSimGUI.changeScene(stage, BPSurgeonSimGUI.loadingView, 300, 320);

        Thread a = new Thread(() -> {
            try {
                BPSurgeonSim.login(username.getText(), password.getText());
            } catch (ExecutionException | InterruptedException e) {
                Platform.runLater(() -> {
                    this.handleLoginError(e.getCause());
                    BPSurgeonSimGUI.changeScene(stage, BPSurgeonSimGUI.loginView, 300, 320);
                });
                return;
            }

            Platform.runLater(() -> {
                System.out.println("Login successful");

                BPSurgeonSimGUI.changeScene(stage, BPSurgeonSimGUI.runningView, 380, 320);
            });
        });
        a.start();
    }

    private void handleLoginError(Throwable e) {
        System.out.println("Login failed: " + e);

        if (e instanceof WrongPasswordException) {
            error.setText("Error logging in, wrong password.");
        } else if (e instanceof HttpResponseException) {
            HttpResponseException responseException = (HttpResponseException) e;
            if (responseException.getStatusCode() == 404) {
                error.setText("Error logging in, user doesn't exist.");
            } else {
                error.setText("Error logging in, " + responseException.getStatusCode() + " " + responseException.getMessage());
            }
        } else {
            error.setText("Error logging in, " + e + ".");
        }
    }
}

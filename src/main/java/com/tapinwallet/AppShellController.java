package com.tapinwallet;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AppShellController implements Initializable {

    @FXML private BorderPane rootPane;       // must match fx:id in FXML
    @FXML private StackPane bodyContainer;   // must match fx:id in FXML

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // load Home page by default
        goHome();
    }

    public void goHome() {
        swapBody("HomeView.fxml");
    }

    public void goSecond() {
        swapBody("SecondView.fxml");
    }

    private void swapBody(String fxmlResource) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlResource));
            Parent content = loader.load();

            // wire the child controller back to this shell for navigation
            Object child = loader.getController();
            if (child instanceof HasHost) {
                ((HasHost) child).setHost(this);
            }

            bodyContainer.getChildren().setAll(content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + fxmlResource, e);
        }
    }

    /** Optional small interface to let child pages get a reference to the shell. */
    public interface HasHost {
        void setHost(AppShellController host);
    }
}

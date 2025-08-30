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
import javafx.scene.Node;

public class AppShellController implements Initializable {

    @FXML
    private BorderPane rootPane;       // must match fx:id in FXML
    @FXML
    private StackPane bodyContainer;   // must match fx:id in FXML

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

    public void swapBody(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(WalletApp.class.getResource(fxmlName));
            Node view = loader.load();

            // Give the child controller access to the host
            Object controller = loader.getController();
            if (controller instanceof HasHost) {
                ((HasHost) controller).setHost(this);
            }

            rootPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Optional small interface to let child pages get a reference to the shell.
     */
    public interface HasHost {

        void setHost(AppShellController host);
    }
}

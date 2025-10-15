package com.tapinwallet.controllers;

import com.tapinwallet.ConfigManager;
import com.tapinwallet.WalletApp;
import com.tapinwallet.data.AppContext;
import com.tapinwallet.data.BaseController;
import com.tapinwallet.util.AppModHelper;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.layout.HBox;

public class AppShellController implements Initializable {

    private AppContext ctx = new AppContext();

    @FXML
    private BorderPane rootPane;

    @FXML
    HBox navBar;
    
    ChangeListener<Node> centerListener;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        centerListener = (obs, oldNode, newNode) -> {
            if (ConfigManager.isWalletConfigured()) {
                navBar.setVisible(true);
                rootPane.centerProperty().removeListener(centerListener);
            } else {
                navBar.setVisible(false);
            }
        };
        rootPane.centerProperty().addListener(centerListener);

        // for testing a second app mod
        AppModHelper.loadModFromResources("proofmanager", "index.xhtml");
        AppModHelper.loadModFromResources("xrpl_wallet", "index.xhtml");
        AppModHelper.loadModFromResources("base_template", "index.xhtml");
    }

    public void goToAppTray() {
        swapBody("AppTrayView.fxml");
    }

    public void goToApp() {
        swapBody("AppModView.fxml");
    }

    public void goToHome() {
        swapBody("HomeView.fxml");
    }
    
    public void goToSettings() {
        swapBody("SettingsView.fxml");
    }

    public void swapBody(String fxmlName) {

        try {
            FXMLLoader loader = new FXMLLoader(WalletApp.class.getResource(fxmlName));
            Node view = loader.load();

            // Give the child controller access to the host
            Object controller = loader.getController();
            if (controller instanceof HasHost) {
                ((HasHost) controller).setHost(this);

                if (controller instanceof BaseController) {
                    ((BaseController) controller).setAppContext(ctx);
                }

            }

            rootPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToAppTray() {
        goToAppTray();
    }

    @FXML
    private void handleBackToHome() {
        goToHome();
    }
    
    @FXML
    private void handleBackToSettings() {
        goToSettings();
    }

    /**
     * Optional small interface to let child pages get a reference to the shell.
     */
    public interface HasHost {

        void setHost(AppShellController host);
    }
}

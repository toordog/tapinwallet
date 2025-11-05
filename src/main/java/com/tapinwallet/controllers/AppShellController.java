package com.tapinwallet.controllers;

import com.tapinwallet.WalletApp;
import com.tapinwallet.data.AppContext;
import com.tapinwallet.data.BaseController;
import com.tapinwallet.util.AppModHelper;
import com.tapinwallet.util.PopupManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class AppShellController implements Initializable {

    public AppContext ctx = new AppContext();

    @FXML
    private BorderPane mainBorderPane;
    
    @FXML private StackPane rootPane;

    @FXML
    HBox navBar;
    
    ChangeListener<Node> centerListener;
    boolean showTools = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        PopupManager.setRootContainer(rootPane);
        
        centerListener = (obs, oldNode, newNode) -> {
            
            if(showTools) {
                navBar.setVisible(true);
                 mainBorderPane.centerProperty().removeListener(centerListener);
            }

        };
        mainBorderPane.centerProperty().addListener(centerListener);

        // for testing a second app mod
        AppModHelper.loadModFromResources("proofmanager", "index.html");
        AppModHelper.loadModFromResources("xrpl_wallet", "index.html");
        AppModHelper.loadModFromResources("base_template", "index.html");
        
        AppModHelper.loadModFromResources("nflpool", "index.html");
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

    public void goToSetup() {
         swapBody("SetupView.fxml");
    }
    
    private void swapBody(String fxmlName) {
        
        if(!fxmlName.equals("SetupView.fxml")) {
            showTools = true;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(WalletApp.class.getResource(fxmlName));
            Node view = loader.load();

            // XXX: here we need to padd data to each controller, maybe by scope?
            
            // Give the child controller access to the host
            Object controller = loader.getController();
            if (controller instanceof HasHost) {
                ((HasHost) controller).setHost(this);

                if (controller instanceof BaseController) {
                    ((BaseController) controller).setAppContext(ctx);
                }

            }
            
            mainBorderPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // WE will bind the new methods above to the fxml click events where needed
    // these will go way
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

package com.tapinwallet.controllers;

import com.tapinwallet.WalletApp;
import com.tapinwallet.data.AppContext;
import com.tapinwallet.data.BaseController;
import com.tapinwallet.util.AppModHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.Node;

public class AppShellController implements Initializable {

    private AppContext ctx = new AppContext();
    
    @FXML
    private BorderPane rootPane;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // for testing a second app mod
        AppModHelper.loadModFromResources("proofmanager", "index.xhtml");
        AppModHelper.loadModFromResources("xrplwallet", "index.xhtml");
        AppModHelper.loadModFromResources("base_template", "index.xhtml");
    }

    public void goHome() {
        swapBody("HomeView.fxml");
    }

    public void goApp() {
        swapBody("AppView.fxml");
    }

    public void swapBody(String fxmlName) {

        try {
            FXMLLoader loader = new FXMLLoader(WalletApp.class.getResource(fxmlName));
            Node view = loader.load();
            
            // Give the child controller access to the host
            Object controller = loader.getController();
            if (controller instanceof HasHost) {
                ((HasHost) controller).setHost(this);
                
                if(controller instanceof BaseController) {
                    ((BaseController) controller).setAppContext(ctx);
                }

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

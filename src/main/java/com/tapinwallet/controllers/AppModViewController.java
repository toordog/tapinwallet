package com.tapinwallet.controllers;

import com.tapinwallet.data.BaseController;
import com.tapinwallet.util.AppModHelper;
import com.tapinwallet.util.JSBridge;
import com.tapinwallet.util.XRPLBridge;
import com.tapinwallet.util.tinydb.DynamicEntity;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.concurrent.Worker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

public class AppModViewController extends BaseController implements AppShellController.HasHost {

    DynamicEntity identity;
            
    private JSBridge jsBridge;
    private final XRPLBridge xrplBridge = new XRPLBridge();
    
    private AppShellController host;

    @Override
    public void onAppContextAvailable() {
        
        this.identity = ctx.context.find("Identity", ctx.id);
        this.jsBridge = new JSBridge(identity);
        
        WebEngine engine = webView.getEngine();

        engine.getLoadWorker().stateProperty().addListener((obs, old, state) -> {
            if (state == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("tapin", jsBridge);
                window.setMember("xrpl", xrplBridge);
            }
        });

        Platform.runLater(() -> {

            headerTitle.setText(ctx.getSelectedMod().name());

            Image icon = AppModHelper.getImage(ctx.getSelectedMod());
            headerIcon.setImage(icon);

            engine.setJavaScriptEnabled(true);
            String url = AppModHelper.loadModFromDisk(ctx.getSelectedMod().hash(), "index.xhtml");
            engine.load(url);
        });
        
    }
    
    @FXML
    WebView webView;

    @FXML
    Label headerTitle;

    @FXML
    ImageView headerIcon, burgerButton;

    @FXML
    private void initialize() throws IOException {

        burgerButton.setOnMouseClicked(e -> {
            System.out.println(e);
        });
        
    }

    @Override
    public void setHost(AppShellController host) {
        this.host = host;
    }

}

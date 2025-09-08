package com.tapinwallet.controllers;

import com.tapinwallet.data.BaseController;
import com.tapinwallet.util.AppModHelper;
import com.tapinwallet.util.JSBridge;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

public class AppViewController extends BaseController implements AppShellController.HasHost {
    
    private final JSBridge bridge = new JSBridge();
    private AppShellController host;

    @FXML
    WebView webView;

    @FXML
    private void initialize() {
        
        WebEngine engine = webView.getEngine();

        engine.getLoadWorker().stateProperty().addListener((obs, old, state) -> {
            if (state == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("tapin", bridge);
            }
        });

        Platform.runLater(() -> {
            engine.setJavaScriptEnabled(true);
            String url = AppModHelper.loadModFromDisk(ctx.getSelectedMod().hash(), "index.xhtml");
            engine.load(url);
        });
    }

    @Override
    public void setHost(AppShellController host) {
        this.host = host;
    }

    @FXML
    private void handleBackHome() {
        if (host != null) {
            host.goHome();
        }
    }

}

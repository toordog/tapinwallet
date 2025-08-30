package com.tapinwallet;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;

public class SecondViewController implements AppShellController.HasHost {

    private AppShellController host;

    @FXML
    WebView webView;

    @FXML // This runs automatically after the FXML is loaded
    private void initialize() {
        webView.getEngine().load("https://google.com");
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

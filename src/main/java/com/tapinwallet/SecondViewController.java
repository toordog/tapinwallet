package com.tapinwallet;

import javafx.fxml.FXML;

public class SecondViewController implements AppShellController.HasHost {

    private AppShellController host;

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

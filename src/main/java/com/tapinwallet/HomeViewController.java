package com.tapinwallet;

import javafx.fxml.FXML;

public class HomeViewController implements AppShellController.HasHost {

    private AppShellController host;

    @Override
    public void setHost(AppShellController host) {
        this.host = host;
    }

    @FXML
    private void handleGoSecond() {
        if (host != null) {
            host.goSecond();
        }
    }
}

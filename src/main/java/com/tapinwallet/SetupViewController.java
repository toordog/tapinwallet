package com.tapinwallet;

import javafx.fxml.FXML;

public class SetupViewController implements AppShellController.HasHost {

    private AppShellController host;

    @Override
    public void setHost(AppShellController host) {
        this.host = host;
    }

    @FXML
    private void handleCreateWallet() {
        // TODO: Generate or import XRPL keys here
        ConfigManager.setWalletConfigured(true);

        if (host != null) {
            host.swapBody("HomeView.fxml");
        }
    }
}

package com.tapinwallet;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WalletApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(WalletApp.class.getResource("AppShell.fxml"));
        Scene scene = new Scene(loader.load(), 400, 800);

        AppShellController controller = loader.getController();
        if (ConfigManager.isWalletConfigured()) {
            controller.swapBody("HomeView.fxml");
        } else {
            controller.swapBody("SetupView.fxml");
        }

        stage.setTitle("Tapin Wallet");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

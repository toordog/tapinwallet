package com.tapinwallet;

import com.tapinwallet.controllers.AppShellController;
import com.tapinwallet.util.PropertyUtil;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WalletApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(WalletApp.class.getResource("AppShell.fxml"));
        Scene scene = new Scene(loader.load(), 425, 900);

        AppShellController controller = loader.getController();

//        I want to convert this to a dynamic entity
        String currentId =  PropertyUtil.get("default", null);

        if(currentId == null) {
            controller.goToSetup();
        } else {
            controller.ctx.id = currentId;
            controller.goToHome();
        }

        stage.setTitle("Tapin Wallet");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}

package com.tapinwallet;

import com.tapinwallet.controllers.AppShellController;
import com.tapinwallet.data.store.IdentityRepository;
import com.tapinwallet.data.store.TapinIdentity;
import com.tapinwallet.data.store.WalletDB;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WalletApp extends Application {

//    static {
//        System.setProperty("objectbox.disableUnpackLib", "true");
//        System.loadLibrary("objectbox-jni");
//    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(WalletApp.class.getResource("AppShell.fxml"));
        Scene scene = new Scene(loader.load(), 425, 900);

        AppShellController controller = loader.getController();

        // let's see if we have an identity first off
        IdentityRepository repo = new IdentityRepository();
        TapinIdentity identity = repo.getById(1L);

        if (identity != null) {
            controller.goToHome();
        } else {
            controller.goToSetup();
        }

        stage.setTitle("Tapin Wallet");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        if (WalletDB.getStore() != null) {
            WalletDB.getStore().close();
        }
    }
}

package com.tapinwallet;

import com.tapinwallet.controllers.AppShellController;
import java.io.IOException;
import java.security.Security;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class WalletApp extends Application {

    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @Override
    public void start(Stage stage) throws IOException {

        for (var p : Security.getProviders()) {
            System.out.println("Provider: " + p.getName());
        }

        FXMLLoader loader = new FXMLLoader(WalletApp.class.getResource("AppShell.fxml"));
        Scene scene = new Scene(loader.load(), 425, 900);

        AppShellController controller = loader.getController();

        // let's see if we have an identity first off
//        IdentityRepository repo = new IdentityRepository();
//        TapinIdentity identity = repo.getById(1L);
//
//        if (identity != null) {
//            controller.goToHome();
//        } else {
//            controller.goToSetup();
//        }
        controller.goToSetup();

        stage.setTitle("Tapin Wallet");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}

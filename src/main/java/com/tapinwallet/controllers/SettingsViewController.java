package com.tapinwallet.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

public class SettingsViewController {

    @FXML private Button copyAddressBtn;
    @FXML private Button exportWalletBtn;
    @FXML private CheckBox darkModeCheck;
    @FXML private CheckBox notificationsCheck;
    @FXML private CheckBox pinCheck;
    @FXML private CheckBox biometricCheck;  
    
    @FXML
    public void initialize() {
        // Simulated handlers
        copyAddressBtn.setOnAction(e -> System.out.println("Copied wallet address"));
        exportWalletBtn.setOnAction(e -> System.out.println("Export wallet"));

        darkModeCheck.selectedProperty().addListener((obs, oldVal, newVal) ->
            System.out.println("Dark mode " + (newVal ? "enabled" : "disabled"))
        );

        notificationsCheck.selectedProperty().addListener((obs, oldVal, newVal) ->
            System.out.println("Notifications " + (newVal ? "enabled" : "disabled"))
        );

        pinCheck.selectedProperty().addListener((obs, oldVal, newVal) ->
            System.out.println("PIN requirement " + (newVal ? "enabled" : "disabled"))
        );

        biometricCheck.selectedProperty().addListener((obs, oldVal, newVal) ->
            System.out.println("Biometric " + (newVal ? "enabled" : "disabled"))
        );
    }
}

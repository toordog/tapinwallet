/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tapinwallet.controllers;

/**
 *
 * @author mike
 */
import javafx.fxml.FXML;
import com.tapinwallet.util.PopupManager;

public class PopupController {

    @FXML
    private void onClose() {
        PopupManager.closePopup();
    }
}
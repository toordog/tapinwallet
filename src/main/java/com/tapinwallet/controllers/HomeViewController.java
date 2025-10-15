/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tapinwallet.controllers;

import com.tapinwallet.data.BaseController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 *
 * @author michael
 */
public class HomeViewController extends BaseController implements Initializable,AppShellController.HasHost {

    AppShellController host;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    @Override
    public void onAppContextAvailable() {
        System.out.println("CTX: "+ctx);
    }

    @Override
    public void setHost(AppShellController host) {
        this.host = host;
    }
}

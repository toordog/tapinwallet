/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tapinwallet.controllers;

import com.tapinwallet.data.BaseController;
import com.tapinwallet.util.tinydb.DynamicEntity;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 *
 * @author michael
 */
public class HomeViewController extends BaseController implements AppShellController.HasHost {

    AppShellController host;
    
    @Override
    public void onAppContextAvailable() {
        DynamicEntity profile = ctx.profiles.find("Identity", ctx.id);
        System.out.println("DID: "+profile.get("did"));
        
        System.out.println("Tokenized: "+profile.tokenizeFields(new String[] {"name","did"}));
    }

    @Override
    public void setHost(AppShellController host) {
        this.host = host;
    }
}

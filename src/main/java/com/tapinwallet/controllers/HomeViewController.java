/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tapinwallet.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tapinwallet.data.BaseController;
import com.tapinwallet.util.tinydb.DynamicEntity;
import java.net.URL;
import java.util.Map;
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
        
        Map<String,String> tokens = profile.tokenizeFields(new String[] {"name","did"});
        System.out.println("Name: "+profile.get("name"));
        System.out.println("Token: "+tokens.get("name"));
        
        System.out.println("DID: "+profile.get("did"));
        System.out.println("Token: "+tokens.get("did"));
    }

    @Override
    public void setHost(AppShellController host) {
        this.host = host;
    }
}

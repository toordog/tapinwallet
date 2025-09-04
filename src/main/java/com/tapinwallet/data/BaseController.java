/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tapinwallet.data;

/**
 *
 * @author michael
 */
public class BaseController {
    protected AppContext ctx;

    public void setAppContext(AppContext ctx) {
        this.ctx = ctx;
        onAppContextAvailable();
    }

    // subclasses can override to react immediately
    protected void onAppContextAvailable() { }
}

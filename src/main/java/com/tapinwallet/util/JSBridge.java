/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tapinwallet.util;

import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

/**
 *
 * @author michael
 */
public class JSBridge {

    // Fully async, no return value
    public void encrypt(String args, JSObject cb) {
        if (cb != null) {
            cb.call("call", new Object[]{null, args, Long.toString(System.currentTimeMillis())}); // XXX : come back to this
        }
    }

    public void sign(String[] args, JSObject cb) {
        String result = "SIGNED(" + args[0] + ")";
        if (cb != null) {
            cb.call("call", new Object[]{result});
        }
    }

    public void deriveKey(String[] args, JSObject cb) {
        String result = "DERIVED(" + args[0] + " + " + args[1] + ")";
        if (cb != null) {
            cb.call("call", new Object[]{result});
        }
    }

}

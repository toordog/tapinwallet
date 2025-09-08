package com.tapinwallet.util;

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

package com.tapinwallet.util;

import com.tapinwallet.data.AppContext;
import com.tapinwallet.util.tinydb.DynamicEntity;
import netscape.javascript.JSObject;

/**
 *
 * @author michael
 */
public class JSBridge {

    DynamicEntity identity;
    
    
    public JSBridge(DynamicEntity identity) {
        this.identity = identity;
    }
    
    public void getDID(JSObject cb) {
        if(cb!=null) {
            String did = this.identity.get("did").toString();
            cb.call("call", new Object[]{null,did});
        }
    }
    
    
    // Fully async, no return value
//    public void encrypt(String args, JSObject cb) {
//        if (cb != null) {
//            cb.call("call", new Object[]{null, args, Long.toString(System.currentTimeMillis())}); // XXX : come back to this
//        }
//    }
//
//    public void sign(String[] args, JSObject cb) {
//        String result = "SIGNED(" + args[0] + ")";
//        if (cb != null) {
//            cb.call("call", new Object[]{result});
//        }
//    }
//
//    public void deriveKey(String[] args, JSObject cb) {
//        String result = "DERIVED(" + args[0] + " + " + args[1] + ")";
//        if (cb != null) {
//            cb.call("call", new Object[]{result});
//        }
//    }
//    
//    public void log(String logLine) {
//        System.out.println(logLine);
//    }

}

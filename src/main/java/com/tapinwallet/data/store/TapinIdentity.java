/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tapinwallet.data.store;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import java.util.Map;

/**
 *
 * @author mike
 */
@Entity
public class TapinIdentity {

    public TapinIdentity() {
    }
    
    @Id
    public Long id;
    
    public String message;
    public Map<String, Object> body;
    public String signature;
    public Map<String, Object> extra;
    
    
    public TapinIdentity(
            String message,
            Map<String, Object> body,
            String signature,
            Map<String, Object> extra) {
        this.message = message;
        this.body = body;
        this.signature = signature;
        this.extra = extra;
    }

//    /**
//     * @return the message
//     */
//    public String getMessage() {
//        return message;
//    }
//
//    /**
//     * @param message the message to set
//     */
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    /**
//     * @return the body
//     */
//    public Map<String, Object> getBody() {
//        return body;
//    }
//
//    /**
//     * @param body the body to set
//     */
//    public void setBody(Map<String, Object> body) {
//        this.body = body;
//    }
//
//    /**
//     * @return the signature
//     */
//    public String getSignature() {
//        return signature;
//    }
//
//    /**
//     * @param signature the signature to set
//     */
//    public void setSignature(String signature) {
//        this.signature = signature;
//    }
//
//    /**
//     * @return the extra
//     */
//    public Map<String, Object> getExtra() {
//        return extra;
//    }
//
//    /**
//     * @param aExtra the extra to set
//     */
//    public void setExtra(Map<String, Object> extra) {
//        this.extra = extra;
//    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tapinwallet.data.store;

import io.objectbox.BoxStore;

/**
 *
 * @author mike
 */
public class WalletDB {
    private static BoxStore store;

    public static synchronized BoxStore getStore() {
        if (store == null) {
            store = MyObjectBox.builder()
                    .name("wallet-db")
                    .build();
        }
        return store;
    }
}
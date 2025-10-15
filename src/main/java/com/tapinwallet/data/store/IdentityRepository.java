/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tapinwallet.data.store;

import io.objectbox.Box;
import java.util.List;

/**
 *
 * @author mike
 */


public class IdentityRepository {

    private final Box<TapinIdentity> box;

    public IdentityRepository() {
        this.box = WalletDB.getStore().boxFor(TapinIdentity.class);
    }

    public void addIdentity(TapinIdentity identity) {
        box.put(identity);
    }

    public List<TapinIdentity> getAll() {
        return box.getAll();
    }

    public TapinIdentity getById(long id) {
        return box.get(id);
    }

    public void clearAll() {
        box.removeAll();
    }
}

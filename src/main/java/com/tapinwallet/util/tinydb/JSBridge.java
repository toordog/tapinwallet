/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tapinwallet.util.tinydb;

/**
 *
 * @author mike
 */
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JSBridge {
    private static final Map<String, Database> dbs = new ConcurrentHashMap<>();

    public Database open(String name, String password) {
        Database db = TinyDB.open(name, password);
        dbs.put(name, db);
        return db;
    }

    public Database get(String name) {
        return dbs.get(name);
    }
}

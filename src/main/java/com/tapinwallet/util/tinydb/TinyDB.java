/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tapinwallet.util.tinydb;

/**
 *
 * @author mike
 */
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class TinyDB {
    private static final Map<String, Database> databases = new ConcurrentHashMap<>();

    public static Database open(String name, String masterPassword) {
        return databases.computeIfAbsent(name, n -> {
            try {
                String path = System.getProperty("user.home") + "/tapinwallet/data/"+name;
                return new Database(name, masterPassword, Path.of(path));
            } catch (Exception e) {
                throw new RuntimeException("Failed to open DB " + name, e);
            }
        });
    }

    public static Database get(String name) {
        return databases.get(name);
    }
}

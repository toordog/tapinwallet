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

public class Database {

    private final String name;
    private final Path basePath;
    private final KeyManager keyManager;
    private final StorageEngine storageEngine;

    public Database(String name, String masterPassword, Path basePath) throws Exception {
        this.name = name;
        this.basePath = basePath;
        this.keyManager = new KeyManager(basePath.resolve("keys"), masterPassword);
        this.storageEngine = new StorageEngine(basePath, keyManager);
    }

    public String getName() {
        return name;
    }

    public Path getBasePath() {
        return basePath;
    }

    public DynamicEntity create(String type) {
        return new DynamicEntity(type, storageEngine);
    }

    public DynamicEntity find(String type, String id) {
        try {
            var data = storageEngine.load(type, id);
            if (data == null) {
                return null;
            }
            DynamicEntity e = new DynamicEntity(type, storageEngine);
            data.forEach(e::set);
            return e;
        } catch (Exception e) {
            throw new RuntimeException("Find failed", e);
        }
    }

    public void rotateKey(String newPassword) {
        try {
            keyManager.rotateMasterKey(newPassword);
        } catch (Exception e) {
            throw new RuntimeException("Rotation failed", e);
        }
    }

    public void unload() {
        storageEngine.unload();
    }

}

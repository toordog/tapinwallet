/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tapinwallet.util.tinydb;

/**
 *
 * @author mike
 */
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicEntity {
    private final String id = UUID.randomUUID().toString();
    private final String type;
    private final Map<String,Object> data = new ConcurrentHashMap<>();
    private final StorageEngine storage;

    public DynamicEntity(String type, StorageEngine storage) {
        this.type = type;
        this.storage = storage;
    }

    public String getId() { return id; }
    public String getType() { return type; }

    public void set(String key, Object value) { data.put(key, value); }
    public Object get(String key) { return data.get(key); }

    public Map<String,Object> getData() { return data; }

    public void persist() {
        try {
            storage.save(type, id, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String tokenizeAll() { return Tokenizer.tokenizeMap(data); }

    public String tokenizeFields(String[] fields) {
        Map<String,Object> subset = new LinkedHashMap<>();
        for(String f: fields) if(data.containsKey(f)) subset.put(f,data.get(f));
        return Tokenizer.tokenizeMap(subset);
    }
}

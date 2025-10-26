/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tapinwallet.util.tinydb;

/**
 *
 * @author mike
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class StorageEngine {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Path basePath;
    private final KeyManager keyManager;
    private final Map<String, Map<String, Long>> index = new HashMap<>();
    private final Map<String, RandomAccessFile> open = new HashMap<>();

    public StorageEngine(Path basePath, KeyManager keyManager) throws IOException {
        this.basePath = basePath;
        this.keyManager = keyManager;
        Files.createDirectories(basePath);
    }

    public synchronized void save(String type, String id, Map<String,Object> obj) throws Exception {
        Path file = basePath.resolve(type + ".db");
        RandomAccessFile raf = open.computeIfAbsent(type, t -> {
            try {
                RandomAccessFile f = new RandomAccessFile(file.toFile(),"rw");
                f.seek(f.length());
                return f;
            } catch(IOException e){ throw new RuntimeException(e); }
        });

        String json = mapper.writeValueAsString(obj);
        byte[] enc = CryptoUtils.encrypt(json.getBytes(), keyManager.getDataKey());
        String line = id + "|" + Base64.getEncoder().encodeToString(enc) + "\n";
        long pos = raf.length();
        raf.seek(pos);
        raf.write(line.getBytes());
        index.computeIfAbsent(type,k->new HashMap<>()).put(id,pos);
    }

    public synchronized Map<String,Object> load(String type, String id) throws Exception {
        Path file = basePath.resolve(type + ".db");
        if(!Files.exists(file)) return null;

        if(!index.containsKey(type)) buildIndex(type,file);
        Long pos = index.get(type).get(id);
        if(pos==null) return null;

        try(RandomAccessFile raf = new RandomAccessFile(file.toFile(),"r")) {
            raf.seek(pos);
            String line = raf.readLine();
            int sep = line.indexOf('|');
            String b64 = line.substring(sep+1);
            byte[] dec = CryptoUtils.decrypt(Base64.getDecoder().decode(b64), keyManager.getDataKey());
            return mapper.readValue(dec, Map.class);
        }
    }

    private void buildIndex(String type, Path file) {
        try(RandomAccessFile raf = new RandomAccessFile(file.toFile(),"r")) {
            Map<String,Long> idx = new HashMap<>();
            long pos = 0; String line;
            while((line=raf.readLine())!=null) {
                int sep = line.indexOf('|');
                if(sep>0) idx.put(line.substring(0,sep), pos);
                pos = raf.getFilePointer();
            }
            index.put(type, idx);
        } catch(IOException e){ e.printStackTrace(); }
    }
}

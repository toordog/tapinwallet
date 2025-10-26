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
import javax.crypto.KeyGenerator;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.*;

public class KeyManager {
    private final Path keyFile;
    private final ObjectMapper mapper = new ObjectMapper();
    private byte[] dataKey;
    private byte[] masterKey;

    public KeyManager(Path keyPath, String masterPassword) throws Exception {
        Files.createDirectories(keyPath.getParent());
        this.keyFile = keyPath;
        this.masterKey = Arrays.copyOf(masterPassword.getBytes(), 32);

        if (Files.exists(keyFile)) {
            Map<String,String> json = mapper.readValue(keyFile.toFile(), Map.class);
            byte[] iv = Base64.getDecoder().decode(json.get("iv"));
            byte[] wrapped = Base64.getDecoder().decode(json.get("wrappedKey"));
            dataKey = CryptoUtils.decrypt(wrapped, masterKey, iv);
        } else {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            dataKey = keyGen.generateKey().getEncoded();
            saveKey(masterKey);
        }
    }

    public byte[] getDataKey() { return dataKey; }

    public void rotateMasterKey(String newPassword) throws Exception {
        masterKey = newPassword.getBytes();
        saveKey(masterKey);
    }

    private void saveKey(byte[] masterBytes) throws Exception {
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        byte[] wrapped = CryptoUtils.encrypt(dataKey, masterBytes, iv);

        Map<String,String> json = new HashMap<>();
        json.put("version","1");
        json.put("iv", Base64.getEncoder().encodeToString(iv));
        json.put("wrappedKey", Base64.getEncoder().encodeToString(wrapped));
        mapper.writeValue(keyFile.toFile(), json);
    }
}

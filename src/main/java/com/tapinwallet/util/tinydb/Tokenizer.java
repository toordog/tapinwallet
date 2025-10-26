/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tapinwallet.util.tinydb;

/**
 *
 * @author mike
 */
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tapinwallet.util.CryptLite;

public class Tokenizer {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String tokenizeMap(Map<String, Object> map) {
        try {
            Map<String, String> tokenized = new LinkedHashMap<>();
            for (var e : map.entrySet()) {
                tokenized.put(e.getKey(), hash(e.getValue()));
            }
            return mapper.writeValueAsString(tokenized);
        } catch (Exception e) {
            throw new RuntimeException("Tokenization failed", e);
        }
    }

    public static String tokenizeFields(Map<String, Object> map, String[] fields) {
        try {
            Map<String, String> tokenized = new LinkedHashMap<>();
            for (String f : fields) {
                if (map.containsKey(f)) tokenized.put(f, hash(map.get(f)));
            }
            return mapper.writeValueAsString(tokenized);
        } catch (Exception e) {
            throw new RuntimeException("Tokenization failed", e);
        }
    }

    private static String hash(Object value) {
        return CryptLite.sha512(value.toString().getBytes(), value.toString().getBytes());
    }
}

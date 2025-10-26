/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tapinwallet.util;

/**
 *
 * @author mike
 */
import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class PropertyUtil {
    private static final Path FILE = Paths.get(System.getProperty("user.home")+"/tapinwallet", "tapin.properties");
    private static final Properties props = new Properties();

    static {
        if (Files.exists(FILE)) {
            InputStream in = null;
            try {
                in = Files.newInputStream(FILE);
                props.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try { in.close(); } catch (IOException ignored) {}
                }
            }
        } else {
            try {
                Files.createFile(FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String get(String key, String def) {
        return props.getProperty(key, def);
    }

    public static void set(String key, String value) {
        props.setProperty(key, value);
        save();
    }

    private static void save() {
        OutputStream out = null;
        try {
            out = Files.newOutputStream(FILE);
            props.store(out, "Tapin Wallet Defaults");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try { out.close(); } catch (IOException ignored) {}
            }
        }
    }
}

package com.tapinwallet;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE = System.getProperty("user.home") + "/.tapinwallet.properties";

    public static boolean isWalletConfigured() {
        Properties props = new Properties();
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            props.load(reader);
            return "true".equals(props.getProperty("wallet.configured"));
        } catch (Exception e) {
            return false;
        }
    }

    public static void setWalletConfigured(boolean configured) {
        Properties props = new Properties();
        props.setProperty("wallet.configured", String.valueOf(configured));
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            props.store(writer, "Tapin Wallet Config");
        } catch (Exception ignored) {}
    }
}

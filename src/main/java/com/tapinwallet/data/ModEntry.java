package com.tapinwallet.data;

/**
 *
 * @author michael
 */
public record ModEntry(String id, String name, String icon, String entryFile, String hash) {
    
    @Override
    public String toString() {
        return name;
    }
}

package com.tapinwallet.data;

/**
 *
 * @author michael
 */
public record ModEntry(String name, String entryFile, String hash) {
    
    @Override
    public String toString() {
        return name;
    }
}

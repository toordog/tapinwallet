package com.tapinwallet.data;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */

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

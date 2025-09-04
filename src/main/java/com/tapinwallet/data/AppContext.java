/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tapinwallet.data;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author michael
 */
public class AppContext {
    
    private final ObjectProperty<ModEntry> selectedMod =
            new SimpleObjectProperty<>();

    public ObjectProperty<ModEntry> selectedModProperty() { return selectedMod; }
    public ModEntry getSelectedMod() { return selectedMod.get(); }
    public void setSelectedMod(ModEntry m) { selectedMod.set(m); }
}

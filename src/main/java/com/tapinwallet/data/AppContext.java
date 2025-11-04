package com.tapinwallet.data;

import com.tapinwallet.util.CryptLite;
import com.tapinwallet.util.tinydb.Database;
import com.tapinwallet.util.tinydb.TinyDB;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author michael
 */
public class AppContext {
    
    private String pass = CryptLite.sha512("defaultpassword".getBytes(), "defaultsalt".getBytes());
    public Database context = TinyDB.open("context", pass);
    
    public String id;
    
    private final ObjectProperty<ModEntry> selectedMod =
            new SimpleObjectProperty<>();

    public ObjectProperty<ModEntry> selectedModProperty() { return selectedMod; }
    public ModEntry getSelectedMod() { return selectedMod.get(); }
    public void setSelectedMod(ModEntry m) { selectedMod.set(m); }
}

package com.tapinwallet.controllers;

import com.tapinwallet.data.BaseController;
import com.tapinwallet.util.AppModHelper;
import com.tapinwallet.data.ModEntry;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class HomeViewController extends BaseController implements AppShellController.HasHost {

    private AppShellController host;

    @FXML
    ListView<ModEntry> appModList;

    @FXML
    public void initialize() {

        appModList.setOnMouseClicked(event -> {
            ModEntry selected = appModList.getSelectionModel().getSelectedItem();

            if (selected != null) {
                
                ctx.setSelectedMod(selected);
                
                if (host != null) {
                    host.goApp();
                }
            }
        });

        List<ModEntry> appmods = AppModHelper.listAvailableModsWithEntries();
        appModList.setItems(FXCollections.observableArrayList(appmods));
    }

    @Override
    public void setHost(AppShellController host) {
        this.host = host;
    }

//    @FXML
//    private void handleGoSecond() {
//        if (host != null) {
//            host.goSecond();
//        }
//    }
//    public List<ModEntry> listAvailableModsWithEntries() {
//        List<ModEntry> mods = new ArrayList<>();
//        Path base = modsBase();
//        try (DirectoryStream<Path> ds = Files.newDirectoryStream(base)) {
//            for (Path dir : ds) {
//                if (!Files.isDirectory(dir)) {
//                    continue;
//                }
//                String modName = dir.getFileName().toString();
//                String entry = null;
//                try (DirectoryStream<Path> fs = Files.newDirectoryStream(dir, "*.xhtml")) {
//                    for (Path f : fs) {
//                        String fn = f.getFileName().toString();
//                        if (!fn.endsWith(".tapin.xhtml")) {
//                            entry = fn;
//                            break;
//                        }
//                    }
//                }
//                if (entry != null) {
//                    mods.add(new ModEntry(modName, entry));
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return mods;
//    }
//
//    // Base directory: default ~/appmods; override with -Dtapin.mods.dir=/path
//    private Path modsBase() {
//        String override = System.getProperty("tapin.mods.dir");
//        Path base = (override != null && !override.isBlank())
//                ? Paths.get(override)
//                : Paths.get(System.getProperty("user.home"), "appmods");
//        try {
//            Files.createDirectories(base);
//        } catch (Exception ignored) {
//        }
//        return base;
//    }
}

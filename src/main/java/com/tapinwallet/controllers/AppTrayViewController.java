package com.tapinwallet.controllers;

import com.tapinwallet.data.BaseController;
import com.tapinwallet.util.AppModHelper;
import com.tapinwallet.data.ModEntry;
import java.io.File;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class AppTrayViewController extends BaseController implements AppShellController.HasHost {

    private AppShellController host;

    @FXML
    TilePane appPane;

    @FXML
    public void initialize() {

        List<ModEntry> appmods = AppModHelper.listAvailableModsWithEntries();
        appmods.stream().forEach(mod -> {

            VBox holder = new VBox();
            holder.setAlignment(Pos.CENTER);
            holder.setSpacing(5);
            holder.getStyleClass().add("app-icon");

            Label label = new Label(mod.name());
            label.getStyleClass().add("app-icon-text");

            ImageView iconView = null;

            if (mod.id().equals("appviewicon")) {

                String basePath = AppModHelper.modsBase().toString();
                String iconPath = basePath + "/" + mod.hash() + "/" + mod.icon();
                File icon = new File(iconPath);
                iconView = new ImageView(new Image(icon.toURI().toString()));

            } else {

                try {
                    String basePath = AppModHelper.modsBase().toString();
                    String iconPath = basePath + "/" + mod.hash() + "/" + mod.icon();
                    File icon = new File(iconPath);

                    if (!icon.exists()) {
                        throw new Exception();
                    }
                    iconView = new ImageView(new Image(icon.toURI().toString()));
                } catch (Exception ex) {
                    // fallback to default icon
                    iconView = new ImageView(new Image(getClass().getResource("/META-INF/img/problem.png").toExternalForm()));
                }

            }

            iconView.getStyleClass().add("app-icon-img");
            iconView.setFitWidth(34);
            iconView.setFitHeight(34);

            final ImageView finalIconView = iconView;

            holder.setOnMousePressed(e -> finalIconView.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("pressed"), true));
            holder.setOnMouseReleased(e -> finalIconView.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("pressed"), false));

            holder.getChildren().addAll(iconView, label);

            holder.setOnMouseClicked(e -> {
                ctx.setSelectedMod(mod);
                if (host != null) {
                    host.goToApp();
                }
            });

            appPane.getChildren().add(holder);

        });

    }

    @Override
    public void setHost(AppShellController host) {
        this.host = host;
    }

}

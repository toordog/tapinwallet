package com.tapinwallet.controllers;

import com.tapinwallet.data.BaseController;
import com.tapinwallet.util.AppModHelper;
import com.tapinwallet.data.ModEntry;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class HomeViewController extends BaseController implements AppShellController.HasHost {

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

            if (mod.id().equals("appviewicon")) {
                ImageView iconView = new ImageView(new Image(getClass().getResource("/META-INF/img/box.png").toExternalForm()));
                iconView.getStyleClass().add("app-icon-img");
                iconView.setFitWidth(32);
                iconView.setFitHeight(32);

                holder.setOnMousePressed(e -> iconView.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("pressed"), true));
                holder.setOnMouseReleased(e -> iconView.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("pressed"), false));
                
                holder.getChildren().addAll(iconView, label);
            } else {
                Label icon = new Label("APP");
                icon.getStyleClass().add("app-icon-img");

                holder.setOnMousePressed(e -> icon.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("pressed"), true));
                holder.setOnMouseReleased(e -> icon.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("pressed"), false));

                holder.getChildren().addAll(icon, label);
            }

            holder.setOnMouseClicked(e -> {
                ctx.setSelectedMod(mod);
                if (host != null) {
                    host.goApp();
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

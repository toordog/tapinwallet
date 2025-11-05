/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tapinwallet.util;

/**
 *
 * @author mike
 */
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class PopupManager {

    private static StackPane rootContainer;
    private static StackPane overlay;

    public static void setRootContainer(StackPane root) {
        rootContainer = root;
    }

    public static void showPopup(String fxmlPath) {
        Platform.runLater(() -> {
            try {
                if (rootContainer == null) {
                    System.err.println("PopupManager: rootContainer not set");
                    return;
                }
                if (overlay != null && rootContainer.getChildren().contains(overlay)) {
                    return;
                }

                FXMLLoader loader = new FXMLLoader(PopupManager.class.getResource(fxmlPath));
                Parent popupRoot = loader.load();

                // Build overlay (full-window dim)
                overlay = new StackPane();
                overlay.setStyle("-fx-background-color: rgba(0,0,0,0.5);");
                overlay.setAlignment(javafx.geometry.Pos.CENTER);
                overlay.prefWidthProperty().bind(rootContainer.widthProperty());
                overlay.prefHeightProperty().bind(rootContainer.heightProperty());

                javafx.scene.Node contentNode;

                if (popupRoot instanceof javafx.scene.layout.Region r) {
                    // Make the popup width = window width - 10px
                    r.maxWidthProperty().bind(rootContainer.widthProperty().subtract(15));
//                    r.setMaxHeight(javafx.scene.layout.Region.USE_COMPUTED_SIZE); // optional
                    contentNode = r;
                } else {
                    // Wrap non-Region roots so we can bind size
                    StackPane wrapper = new StackPane(popupRoot);
                    wrapper.setAlignment(javafx.geometry.Pos.CENTER);
                    wrapper.maxWidthProperty().bind(rootContainer.widthProperty().subtract(15));
                    contentNode = wrapper;
                }

                // Keep some gap from edges (outside margin)
                StackPane.setMargin(contentNode, new javafx.geometry.Insets(5));

                // Don’t close when clicking inside the popup
                contentNode.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_CLICKED, e -> e.consume());

                overlay.getChildren().add(contentNode);
                rootContainer.getChildren().add(overlay);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void closePopup() {
        Platform.runLater(() -> {
            if (rootContainer != null && overlay != null) {
                rootContainer.getChildren().remove(overlay);
                overlay = null;
            }
        });
    }
}

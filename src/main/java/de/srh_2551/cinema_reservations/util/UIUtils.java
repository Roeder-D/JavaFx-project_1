package de.srh_2551.cinema_reservations.util;

import javafx.scene.control.Alert;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

public class UIUtils {
    public static void showErrorPopup(Window owner, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);

        if(owner != null){
            alert.initOwner(owner);
        }


        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void resizeAndCenterWindow(Stage stage) {
        stage.sizeToScene();
        //check for screen size
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        //limit stage size
        if (stage.getHeight() > screenBounds.getHeight()) stage.setHeight(screenBounds.getHeight());
        if (stage.getWidth() > screenBounds.getWidth()) stage.setWidth(screenBounds.getWidth());
        //center window on Screen
        stage.centerOnScreen();
    }
}
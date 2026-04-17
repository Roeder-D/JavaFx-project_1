package de.srh_2551.cinema_reservations.app;

import de.srh_2551.cinema_reservations.util.LanguageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ReservationApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ReservationApplication.class.getResource("/de/srh_2551/cinema_reservations/cinema_reservations.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle(LanguageManager.getString("window.title"));

        //Load the CSS file
        String css = Objects.requireNonNull(getClass().getResource("/de/srh_2551/cinema_reservations/style.css")).toExternalForm();
        scene.getStylesheets().add(css);

        //Set min size
        stage.setMinWidth(600);
        stage.setMinHeight(400);

        stage.setScene(scene);
        stage.show();
    }
}

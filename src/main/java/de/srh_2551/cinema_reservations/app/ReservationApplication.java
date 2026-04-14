package de.srh_2551.cinema_reservations.app;

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
        Scene scene = new Scene(fxmlLoader.load(), 1000, 660);
        stage.setTitle("Reservierung");

        // Load the CSS file
        String css = Objects.requireNonNull(getClass().getResource("/de/srh_2551/cinema_reservations/style.css")).toExternalForm();
        scene.getStylesheets().add(css);

        stage.setScene(scene);
        stage.show();
    }
}

module de.srh_.cinema_reservations {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.logging;

    //opens de.srh_2551.cinema_reservations to javafx.fxml;
    //exports de.srh_2551.cinema_reservations;
    exports de.srh_2551.cinema_reservations.app;
    opens de.srh_2551.cinema_reservations.app to javafx.fxml;
    exports de.srh_2551.cinema_reservations.controller;
    opens de.srh_2551.cinema_reservations.controller to javafx.fxml;
}
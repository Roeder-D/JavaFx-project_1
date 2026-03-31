package de.srh_2551.cinema_reservations.controller;

import de.srh_2551.cinema_reservations.model.Hall;
import de.srh_2551.cinema_reservations.model.Row;
import de.srh_2551.cinema_reservations.model.Seat;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ReservationController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");

    }
    //create hall
    private Hall mainHall = new Hall("Main Hall");

    @FXML
    public void initialize() {
        String[] rowLetters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        int rowId = 0;
        for (String letter : rowLetters) {
            Seat.SeatType type = Seat.SeatType.STANDARD;

            mainHall.addRow(new Row(letter, rowId, 15, type));
            rowId++;
        }
    }




}

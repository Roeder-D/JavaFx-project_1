package de.srh_2551.cinema_reservations.controller;

import de.srh_2551.cinema_reservations.data.CsvManager;
import de.srh_2551.cinema_reservations.model.Hall;
import de.srh_2551.cinema_reservations.model.Row;
import de.srh_2551.cinema_reservations.model.Seat;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class ReservationController {

    @FXML
    private VBox defaultView;

    @FXML
    private GridPane seatGrid;

    @FXML
    private ComboBox hallComboBox;


    @FXML
    public void initialize() {
        showDefaultView();
        populateHallMenu();
    }

//Dynamic setup
   private void populateHallMenu(){
        //Look for files
       List<String> hallNames = CsvManager.getAllHallNames();

       //remove existing entries
       hallComboBox.getItems().clear();
       hallComboBox.getItems().addAll(hallNames);

       hallComboBox.setOnAction(event -> {
           String selectedHall = hallComboBox.getValue().toString();

           if (selectedHall != null) {
               Hall loadedHall = CsvManager.loadHall(selectedHall);

               createSeatPlan(loadedHall);
           }
       });
   }


//helper
    //Switching views
    private void showDefaultView() {
        defaultView.setVisible(true);
        seatGrid.setVisible(false);
    }

    private void showSeatGrid(){
        defaultView.setVisible(false);
        seatGrid.setVisible(true);
    }

    //Generating seats
    private Button createSeatButton(String rowName, Seat seat) {
        Button seatBtn = new Button(rowName + "-" + seat.getSeatNumber());
        seatBtn.setPrefSize(40, 40);

        //set colors
        Seat.SeatStatus seatStatus = seat.getSeatStatus();
        if(seatStatus == Seat.SeatStatus.FREE){
            seatBtn.setStyle("-fx-background-color: lightgreen;");
        } else if (seatStatus == Seat.SeatStatus.RESERVED || seatStatus == Seat.SeatStatus.BOOKED) {
            seatBtn.setStyle("-fx-background-color: gray;");
            seatBtn.setDisable(true);
        }else{
            seatBtn.setStyle("-fx-background-color: red;");
            seatBtn.setDisable(true);
        }

        //Event
        seatBtn.setOnAction(event -> {
            seatBtn.setStyle("-fx-background-color: yellow;");
            //TODO: add seat to basket
            //TODO: change seatStatus
            //TODO: deselect
        });

        return seatBtn;
    }

    private void createSeatPlan(Hall hall) {
        seatGrid.getChildren().clear();

        int rowIndex = 0;
        for(Row row : hall.getRows()) {
            String rowName = row.getRowIdentifier();
            int colIndex = 0;
            for(Seat seat : row.getSeats()) {
                Button seatBtn = createSeatButton(rowName, seat);

                seatGrid.add(seatBtn, colIndex, rowIndex);
                colIndex++;
            }
            rowIndex++;
        }
        //Switch view
        showSeatGrid();
    }


  /*  //create hall
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
    }*/




}

package de.srh_2551.cinema_reservations.controller;

import de.srh_2551.cinema_reservations.data.CsvManager;
import de.srh_2551.cinema_reservations.model.Basket;
import de.srh_2551.cinema_reservations.model.Hall;
import de.srh_2551.cinema_reservations.model.Row;
import de.srh_2551.cinema_reservations.model.Seat;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ReservationController {

    // ===================
    //FXML VARIABLES
    // ===================
    @FXML
    private VBox defaultView;
    @FXML
    private VBox seatContainer;
    @FXML
    private ComboBox<String> hallComboBox;
    @FXML
    private BorderPane basketView;
    @FXML
    private Button switchBasketBtn;
    @FXML
    private ListView<String> basketSeatList;
    @FXML
    private Label totalPriceLabel;

    // ===================
    //Data
    // ===================
    private Basket currentBasket;

    // ===================
    //Initialisation
    // ===================

    @FXML
    public void initialize() {
        showDefaultView();
        populateHallMenu();
        fixComboBoxRenderer();
    }

   private void populateHallMenu(){
        //Look for files
       List<String> hallNames = CsvManager.getAllHallNames();

       //remove existing entries
       hallComboBox.getItems().clear();
       hallComboBox.getItems().addAll(hallNames);

       hallComboBox.setOnAction(event -> {
           if(hallComboBox.getValue() != null){
           String selectedHall = hallComboBox.getValue();

           if (selectedHall != null) {
               Hall loadedHall = CsvManager.loadHall(selectedHall);

               //Load hall
               currentBasket = new Basket(loadedHall);

               createSeatPlan(loadedHall);
           }}
       });
   }

    // ===================
    //FXML action handlers
    // ===================
    @FXML
    private void handleCancelBtnClick() {
        currentBasket = null;

        hallComboBox.setValue(null);
        hallComboBox.getSelectionModel().clearSelection();

        showDefaultView();

        //resize window
        Stage stage = (Stage) defaultView.getScene().getWindow();
        stage.sizeToScene();
    }
    private void handleSeatClick(Button seatBtn, Seat seat) {
        try {
            if (seat.getSeatStatus() == Seat.SeatStatus.SELECTED) {
                currentBasket.removeSeat(seat);
            } else if (seat.getSeatStatus() == Seat.SeatStatus.FREE) {
                currentBasket.addSeat(seat);
            }
            applySeatStyle(seatBtn, seat);

        } catch (IllegalStateException e) {
            showErrorPopup("Ungültige Auswahl", e.getMessage());
        }
    }

    @FXML
    private void handleConfirmOrderClick(){
        currentBasket.confirmOrder();
        currentBasket.clearSeats();

        //Save to csv
        CsvManager.saveHall(currentBasket.getSelectedHall());

        //Return to start page
        showDefaultView();
    }

    // ===================
    //View switchers & updaters
    // ===================
    private void showDefaultView() {
        defaultView.setVisible(true);
        seatContainer.setVisible(false);
        basketView.setVisible(false);

        switchBasketBtn.setVisible(false);
    }

    private void showSeatGrid(){
        defaultView.setVisible(false);
        seatContainer.setVisible(true);
        basketView.setVisible(false);

        switchBasketBtn.setVisible(true);
        switchBasketBtn.setText("Warenkorb");
        switchBasketBtn.setOnAction(event -> {
            showBasketView();
        });
    }

    private void showBasketView(){
        defaultView.setVisible(false);
        seatContainer.setVisible(false);
        basketView.setVisible(true);

        switchBasketBtn.setVisible(true);
        switchBasketBtn.setText("Saal");
        switchBasketBtn.setOnAction(event -> {
            showSeatGrid();
        });

        //generate list & price
        refreshBasketList();
        updatePriceLabel();
    }
    private void refreshBasketList(){
        basketSeatList.getItems().clear();
        for(Seat seat : currentBasket.getSelectedSeats()){
            basketSeatList.getItems().add("Sitz: " + currentBasket.getRowIdentifier() + "-" + seat.getSeatNumber() + " Preis: " + seat.getSeatType().getPrice());
        }
    }

    private void updatePriceLabel(){
       double price = currentBasket.getPrice();
       totalPriceLabel.setText("Gesamtpreis: " + String.format("%.2f", price) + "€");
    }

    // ===================
    //UI builders & logic
    // ===================

    private void showErrorPopup(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //build seat plan UI
    private void createSeatPlan(Hall hall) {
        seatContainer.getChildren().clear();


        for(Row row : hall.getRows()){
            String rowName = row.getRowIdentifier();

            HBox rowBox = new HBox(5);//5 pixel gap between seats
            rowBox.setAlignment(Pos.CENTER);

            for(Seat seat : row.getSeats()){
                Button seatBtn = createSeatButton(rowName, seat);
                rowBox.getChildren().add(seatBtn);
            }

            seatContainer.getChildren().add(rowBox);
        }

        //Switch view
        showSeatGrid();

        //resize window
        Platform.runLater(() -> {
            Stage stage = (Stage) seatContainer.getScene().getWindow();
            stage.sizeToScene();
        });

    }

    //Generating seats
    private Button createSeatButton(String rowIdentifier, Seat seat) {
        Button seatBtn = new Button(rowIdentifier + "-" + seat.getSeatNumber());
        seatBtn.setPrefSize(50, 50);

        applySeatStyle(seatBtn, seat);

        seatBtn.setOnAction(event -> {handleSeatClick(seatBtn, seat);});

        return seatBtn;
    }

    private void applySeatStyle(Button seatBtn, Seat seat){
        seatBtn.getStyleClass().setAll("button", "seat-button");

        switch(seat.getSeatStatus()) {
            case FREE:
                seatBtn.getStyleClass().add("seat-free");
                seatBtn.setDisable(false);
                break;
            case SELECTED:
                seatBtn.getStyleClass().add("seat-selected");
                seatBtn.setDisable(false);
                break;
            case RESERVED, BOOKED:
                seatBtn.getStyleClass().add("seat-booked");
                seatBtn.setDisable(true);
                break;
            default:
                seatBtn.getStyleClass().add("seat-error");
                seatBtn.setDisable(true);
                break;
        }

        switch(seat.getSeatType()){
            case STANDARD:
                seatBtn.getStyleClass().add("type-standard");
                break;
            case PREMIUM:
                seatBtn.getStyleClass().add("type-premium");
                break;
            case DELUXE:
                seatBtn.getStyleClass().add("type-deluxe");
                break;
            default:
                break;
        }
    }
    //manually filling the comboBox to fix bug with default renderer
    private void fixComboBoxRenderer(){
        hallComboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(hallComboBox.getPromptText());
                } else {
                    setText(item);
                }
            }
        });
    }

}

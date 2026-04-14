package de.srh_2551.cinema_reservations.controller;

import de.srh_2551.cinema_reservations.data.CsvManager;
import de.srh_2551.cinema_reservations.model.Basket;
import de.srh_2551.cinema_reservations.model.Hall;
import de.srh_2551.cinema_reservations.model.Row;
import de.srh_2551.cinema_reservations.model.Seat;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

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
    private ScrollPane seatScrollPane;
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
           String selectedHall = hallComboBox.getValue();

           if (selectedHall != null) {
               Hall loadedHall = CsvManager.loadHall(selectedHall);

               //Load hall
               currentBasket = new Basket(loadedHall);

               createSeatPlan(loadedHall);
           }
       });
   }

    // ===================
    //FXML action handlers
    // ===================
    @FXML
    private void handleCancelBtnClick() {
        //cancel order
        if(currentBasket != null){
            currentBasket.cancelOrder();
        }
        //destroy basket
        currentBasket = null;

        hallComboBox.setValue(null);
        hallComboBox.getSelectionModel().clearSelection();

        showDefaultView();

        resizeWindow();
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

        //Save to csv
        CsvManager.saveHall(currentBasket.getSelectedHall());

        //Return to start page
        showDefaultView();

        resizeWindow();
    }

    // ===================
    //View switchers & updaters
    // ===================
    private void showDefaultView() {
        defaultView.setVisible(true);
        seatScrollPane.setVisible(false);
        basketView.setVisible(false);

        switchBasketBtn.setVisible(false);

        populateHallMenu();
        fixComboBoxRenderer();
    }

    private void showSeatGrid(){
        defaultView.setVisible(false);
        seatScrollPane.setVisible(true);
        basketView.setVisible(false);

        switchBasketBtn.setVisible(true);
        switchBasketBtn.setText("Warenkorb");
        switchBasketBtn.setOnAction(event -> showBasketView());
    }

    private void showBasketView(){
        defaultView.setVisible(false);
        seatScrollPane.setVisible(false);
        basketView.setVisible(true);

        switchBasketBtn.setVisible(true);
        switchBasketBtn.setText("Saal");
        switchBasketBtn.setOnAction(event -> showSeatGrid());

        //generate list & price
        refreshBasketList();
        updatePriceLabel();

        //TODO: Make this shine and add cancelSeat() and discount options
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

        int rowCount = 0;
        for(Row row : hall.getRows()){
            String rowName = row.getRowIdentifier();

            //create gap between two rows
            if(row.getGapInFront()){
                HBox gapBox = new HBox();
                gapBox.setPrefHeight(10);
                seatContainer.getChildren().add(gapBox);
            }

            HBox rowBox = new HBox(5);//5 pixel gap between seats
            rowBox.setAlignment(Pos.CENTER);

            //stagger rows
            if(rowCount % 2 == 0){
                rowBox.setTranslateX(25);
            }

            for(Seat seat : row.getSeats()){
                Button seatBtn = createSeatButton(rowName, seat);
                rowBox.getChildren().add(seatBtn);
            }
            rowCount ++;
            seatContainer.getChildren().add(rowBox);
        }

        //Switch view
        showSeatGrid();

        resizeWindow();
    }

    //Generating seats
    private Button createSeatButton(String rowIdentifier, Seat seat) {
        Button seatBtn = new Button(rowIdentifier + "-" + seat.getSeatNumber());
        seatBtn.setPrefSize(50, 50);

        applySeatStyle(seatBtn, seat);

        seatBtn.setOnAction(event -> handleSeatClick(seatBtn, seat));

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

    private void resizeWindow(){
        Stage stage = (Stage) defaultView.getScene().getWindow();
        stage.sizeToScene();

        //check for screen size
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        //limit stage size
        if (stage.getHeight() > screenBounds.getHeight()) {
            stage.setHeight(screenBounds.getHeight());
        }
        if (stage.getWidth() > screenBounds.getWidth()) {
            stage.setWidth(screenBounds.getWidth());
        }
        //center window on screen
        stage.centerOnScreen();
    }

}

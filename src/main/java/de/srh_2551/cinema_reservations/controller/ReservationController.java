package de.srh_2551.cinema_reservations.controller;

import de.srh_2551.cinema_reservations.data.CsvManager;
import de.srh_2551.cinema_reservations.model.Basket;
import de.srh_2551.cinema_reservations.model.Hall;
import de.srh_2551.cinema_reservations.model.Row;
import de.srh_2551.cinema_reservations.model.Seat;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class ReservationController {

    //=====================
    //Style
    //=====================
    //Seat status
    private static final String STYLE_FREE = "-fx-background-color: lightgreen; -fx-opacity: 1;";
    private static final String STYLE_BOOKED = "-fx-background-color: gray; -fx-opacity: 1;";
    private static final String STYLE_SELECTED = "-fx-background-color: yellow; -fx-opacity: 1;";
    private static final String STYLE_ERROR = "-fx-background-color: red; -fx-opacity: 1;";
    //Seat type
    private static final String STYLE_STANDARD = "-fx-border-color: darkgray; -fx-border-width: 1px; -fx-border-radius: 4px;-fx-background-radius: 4px;-fx-background-insets: 0;";
    private static final String STYLE_PREMIUM = "-fx-border-color: silver; -fx-border-width: 3px; -fx-border-radius: 4px;-fx-background-radius: 4px;-fx-background-insets: 1;";
    private static final String STYLE_DELUXE = "-fx-border-color: gold; -fx-border-width: 4px; -fx-border-radius: 4px;-fx-background-radius: 4px;-fx-background-insets: 2;";

    // ===================
    //FXML VARIABLES
    // ===================
    @FXML
    private VBox defaultView;
    @FXML
    private GridPane seatGrid;
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
        seatGrid.setVisible(false);
        basketView.setVisible(false);

        switchBasketBtn.setVisible(false);
    }

    private void showSeatGrid(){
        defaultView.setVisible(false);
        seatGrid.setVisible(true);
        basketView.setVisible(false);

        switchBasketBtn.setVisible(true);
        switchBasketBtn.setText("Warenkorb");
        switchBasketBtn.setOnAction(event -> {
            showBasketView();
        });
    }

    private void showBasketView(){
        defaultView.setVisible(false);
        seatGrid.setVisible(false);
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
            basketSeatList.getItems().add("Sitz: " + currentBasket.getRowIdentifier() + "-" + seat.getSeatNumber());
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

    //Generating seats
    private Button createSeatButton(String rowIdentifier, Seat seat) {
        Button seatBtn = new Button(rowIdentifier + "-" + seat.getSeatNumber());
        seatBtn.setPrefSize(50, 50);

        applySeatStyle(seatBtn, seat);

        seatBtn.setOnAction(event -> {handleSeatClick(seatBtn, seat);});

        return seatBtn;
    }

    private void applySeatStyle(Button seatBtn, Seat seat){
        String style;

        switch(seat.getSeatStatus()) {
            case FREE:
                style = STYLE_FREE;
                seatBtn.setDisable(false);
                break;
            case SELECTED:
                style = STYLE_SELECTED;
                seatBtn.setDisable(false);
                break;
            case RESERVED:
            case BOOKED:
                style = STYLE_BOOKED;
                seatBtn.setDisable(true);
                break;
            default:
                style = STYLE_ERROR;
                seatBtn.setDisable(true);
                break;
        }

        switch(seat.getSeatType()){
            case STANDARD:
                style += STYLE_STANDARD;
                break;
            case PREMIUM:
                style += STYLE_PREMIUM;
                break;
            case DELUXE:
                style += STYLE_DELUXE;
                break;
            default:
                break;
        }
        seatBtn.setStyle(style);
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

package de.srh_2551.cinema_reservations.controller;

import de.srh_2551.cinema_reservations.data.CsvManager;
import de.srh_2551.cinema_reservations.model.Basket;
import de.srh_2551.cinema_reservations.model.Hall;
import de.srh_2551.cinema_reservations.model.Row;
import de.srh_2551.cinema_reservations.model.Seat;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class ReservationController {

    //=====================
    //Style
    //=====================
    private static final String STYLE_FREE = "-fx-background-color: lightgreen; -fx-opacity: 1;";
    private static final String STYLE_BOOKED = "-fx-background-color: gray; -fx-opacity: 1;";
    private static final String STYLE_SELECTED = "-fx-background-color: yellow; -fx-opacity: 1;";
    private static final String STYLE_ERROR = "-fx-background-color: red; -fx-opacity: 1;";


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
    private Button cancelBtn;
    @FXML
    private Button confirmOrderBtn;
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

        //manually filling the comboBox to fix bug with default renderer
        hallComboBox.setButtonCell(new javafx.scene.control.ListCell<String>() {
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
        switch (seat.getSeatStatus()) {
            case FREE:
                seatBtn.setStyle(STYLE_FREE);
                seatBtn.setDisable(false);
                break;
            case SELECTED:
                seatBtn.setStyle(STYLE_SELECTED);
                seatBtn.setDisable(false); // Make sure this stays clickable!
                break;
            case RESERVED:
            case BOOKED:
                seatBtn.setStyle(STYLE_BOOKED);
                seatBtn.setDisable(true);
                break;
            default:
                seatBtn.setStyle(STYLE_ERROR);
                seatBtn.setDisable(true);
                break;
        }
        //TODO: create seat types
    }

    private void handleSeatClick(Button seatBtn, Seat seat) {
        if (seat.getSeatStatus() == Seat.SeatStatus.SELECTED) {
            currentBasket.removeSeat(seat);
            applySeatStyle(seatBtn, seat);
            // TODO: check adjacent after removal

        } else if (seat.getSeatStatus() == Seat.SeatStatus.FREE) {
            try {
                currentBasket.addSeat(seat);
                applySeatStyle(seatBtn, seat);
            } catch (Exception e) {
                // TODO: display error (adjacent)
            }
        }
    }

}

package de.srh_2551.cinema_reservations.controller;

import de.srh_2551.cinema_reservations.util.LanguageManager;
import de.srh_2551.cinema_reservations.data.CsvManager;
import de.srh_2551.cinema_reservations.model.Basket;
import de.srh_2551.cinema_reservations.model.Hall;
import de.srh_2551.cinema_reservations.model.Seat;
import de.srh_2551.cinema_reservations.util.SeatUIBuilder;
import de.srh_2551.cinema_reservations.util.UIUtils;
import de.srh_2551.cinema_reservations.view.BasketItemCell;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.*;

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
    private ComboBox<String> selectHallComboBox;
    @FXML
    private BorderPane basketView;
    @FXML
    private Button switchBasketBtn;
    @FXML
    private ListView<Seat> basketSeatList;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private MenuButton discountMenuBtn;
    @FXML
    public Label hallNameLabel;
    @FXML
    public ComboBox<String> languageComboBox;
    @FXML
    public Button confirmBtn;
    @FXML
    public Button cancelBtn;
    @FXML
    public Label defaultMessageLabel;
    @FXML
    public Label basketTitleLabel;

    // ===================
    //Data
    // ===================
    private Basket currentBasket;
    private final Map<Seat, Button> seatButtonMap = new HashMap<>();
    private Basket.Discounts activeDiscount = Basket.Discounts.DEFAULT;

    // ===================
    //Initialisation
    // ===================

    @FXML
    public void initialize() {
        // Bind managed to visible so hidden views release their layout space (Fix automatic window size)
        defaultView.managedProperty().bind(defaultView.visibleProperty());
        seatScrollPane.managedProperty().bind(seatScrollPane.visibleProperty());
        basketView.managedProperty().bind(basketView.visibleProperty());
        switchBasketBtn.managedProperty().bind(switchBasketBtn.visibleProperty());
        hallNameLabel.managedProperty().bind(hallNameLabel.visibleProperty());
        selectHallComboBox.managedProperty().bind(selectHallComboBox.visibleProperty());

        showDefaultView();
        populateHallMenu();
        fixComboBoxRenderer();
        createBasketList();
        setupLanguageMenu();
    }

   private void populateHallMenu(){
        //Look for files
       List<String> hallNames = CsvManager.getAllHallNames();

       //remove existing entries
       selectHallComboBox.getItems().clear();
       selectHallComboBox.getItems().addAll(hallNames);

       selectHallComboBox.setOnAction(ignored -> {
           String selectedHall = selectHallComboBox.getValue();

           if (selectedHall != null) {
               Hall loadedHall = CsvManager.loadHall(selectedHall);
               currentBasket = new Basket(loadedHall);

               hallNameLabel.setText(loadedHall.getName());

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

        selectHallComboBox.setValue(null);
        selectHallComboBox.getSelectionModel().clearSelection();

        activeDiscount = Basket.Discounts.DEFAULT;
        discountMenuBtn.setText(LanguageManager.getString("discount.DEFAULT"));
        showDefaultView();

        UIUtils.resizeAndCenterWindow((Stage) defaultView.getScene().getWindow());
    }
    private void handleSeatClick(Seat seat) {
        try {
            if (seat.getSeatStatus() == Seat.SeatStatus.SELECTED) {
                currentBasket.removeSeat(seat);
            } else if (seat.getSeatStatus() == Seat.SeatStatus.FREE) {
                currentBasket.addSeat(seat);
            }

            SeatUIBuilder.applySeatStyle(seatButtonMap.get(seat), seat);
        } catch (IllegalStateException e) {
            UIUtils.showErrorPopup(LanguageManager.getString("error.invalidSelection"), e.getMessage());
        }
    }

    @FXML
    private void handleConfirmOrderClick(){
        currentBasket.confirmOrder();
        activeDiscount = Basket.Discounts.DEFAULT;
        discountMenuBtn.setText(LanguageManager.getString("discount.DEFAULT"));

        //Save to csv
        CsvManager.saveHall(currentBasket.getSelectedHall());

        //Return to start page
        showDefaultView();

        UIUtils.resizeAndCenterWindow((Stage) defaultView.getScene().getWindow());
    }

    // ===================
    //View switchers & updaters
    // ===================
    private void showDefaultView() {
        defaultView.setVisible(true);
        seatScrollPane.setVisible(false);
        basketView.setVisible(false);

        selectHallComboBox.setVisible(true);
        hallNameLabel.setVisible(false);

        switchBasketBtn.setVisible(false);

        populateHallMenu();
        fixComboBoxRenderer();
    }

    private void showSeatGrid(){
        defaultView.setVisible(false);
        seatScrollPane.setVisible(true);
        basketView.setVisible(false);

        selectHallComboBox.setVisible(true);
        hallNameLabel.setVisible(false);

        switchBasketBtn.setVisible(true);
        switchBasketBtn.setText(LanguageManager.getString("switchBasket.btn.toBasket"));
        switchBasketBtn.setOnAction(ignored -> showBasketView());
    }

    private void showBasketView(){
        defaultView.setVisible(false);
        seatScrollPane.setVisible(false);
        basketView.setVisible(true);

        selectHallComboBox.setVisible(false);
        hallNameLabel.setVisible(true);

        switchBasketBtn.setVisible(true);
        switchBasketBtn.setText(LanguageManager.getString("switchBasket.btn.toHall"));
        switchBasketBtn.setOnAction(ignored -> updateSeatGrid());
        updateBasketList();
        updatePriceLabel();
    }

    private void updateSeatGrid(){
        if (seatButtonMap.isEmpty()) {
            createSeatPlan(currentBasket.getSelectedHall());
        } else {
            //update style for seats
            for (Map.Entry<Seat, Button> entry : seatButtonMap.entrySet()) {
                Seat seat = entry.getKey();
                Button seatBtn = entry.getValue();

                SeatUIBuilder.applySeatStyle(seatBtn, seat);
            }
            showSeatGrid();
        }
    }

    private void updateBasketList(){
        basketSeatList.getItems().clear();
        if(currentBasket != null){
            basketSeatList.getItems().addAll(currentBasket.getSelectedSeats());
        }
    }

    private void updatePriceLabel(){
        if(currentBasket != null) {
            BigDecimal price = currentBasket.getPrice();
            totalPriceLabel.setText(LanguageManager.getString("price.total") + " " + String.format("%.2f", price) + "€");
        }
    }

    // ===================
    //UI builders & logic
    // ===================

    private void updateLanguage(){
        discountMenuBtn.setText(LanguageManager.getString("discount." + activeDiscount.name()));

        //create discount menu items
        if (discountMenuBtn.getItems().isEmpty()) {
            for (Basket.Discounts discount : Basket.Discounts.values()) {
                MenuItem item = new MenuItem();
                item.setUserData(discount); //attaches enum to the button

                item.setOnAction(ignored -> {
                    activeDiscount = discount;
                    discountMenuBtn.setText(item.getText());

                    if (currentBasket != null) {
                        currentBasket.setCurrentDiscount(discount);
                        updatePriceLabel();
                    }
                });
                discountMenuBtn.getItems().add(item);
            }
        }
        //update menu items
        for (MenuItem item : discountMenuBtn.getItems()) {
            Basket.Discounts discount = (Basket.Discounts) item.getUserData(); //retrieves the attached Enum
            item.setText(LanguageManager.getString("discount." + discount.name()));
        }

        selectHallComboBox.setPromptText(LanguageManager.getString("selectHall.prompt"));
        defaultMessageLabel.setText(LanguageManager.getString("default.message"));
        basketTitleLabel.setText(LanguageManager.getString("basket.title"));
        cancelBtn.setText(LanguageManager.getString("btn.cancel"));
        confirmBtn.setText(LanguageManager.getString("btn.confirm"));

        //switch dynamic label
        if(basketView.isVisible()){
            switchBasketBtn.setText(LanguageManager.getString("switchBasket.btn.toHall"));
        } else if (seatScrollPane.isVisible()) {
            seatButtonMap.clear();
            switchBasketBtn.setText(LanguageManager.getString("switchBasket.btn.toBasket"));
        }
        if(defaultView.getScene() != null) {
            Stage stage = (Stage) defaultView.getScene().getWindow();
            stage.setTitle(LanguageManager.getString("window.title"));
        }

        updatePriceLabel();

        //reload UI-items
        if(currentBasket != null) {
            updateBasketList();
            if(seatScrollPane.isVisible()) {
                updateSeatGrid();
            }
        }
    }

    //build seat plan UI
    private void createSeatPlan(Hall hall) {
        //moved UI creation
        SeatUIBuilder.buildSeatGrid(hall, seatContainer, seatButtonMap, this::handleSeatClick);

        //Switch view
        showSeatGrid();
        UIUtils.resizeAndCenterWindow((Stage) defaultView.getScene().getWindow());
    }

    private void createBasketList() {
        //moved the UI code in separate class
        //cell factory only renders visible cells
        basketSeatList.setCellFactory(_ -> new BasketItemCell(currentBasket, seat -> {
            //handles remove button in cell
            try {
                currentBasket.removeSeat(seat);
                updateBasketList();
                updatePriceLabel();
            } catch (IllegalStateException e) {
                UIUtils.showErrorPopup(LanguageManager.getString("error.cannotRemove"), e.getMessage());
            }
        }));
    }

    //manually filling the comboBox to fix bug with default renderer
    private void fixComboBoxRenderer(){
        selectHallComboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(selectHallComboBox.getPromptText());
                } else {
                    setText(item);
                }
            }
        });
    }

    private void setupLanguageMenu(){
        languageComboBox.getItems().clear();
        languageComboBox.getItems().addAll("Deutsch", "English");
        languageComboBox.setValue("Deutsch");

        languageComboBox.setOnAction(ignored -> {
            String selection = languageComboBox.getValue();
            if(selection.equals("English")){
                LanguageManager.setLanguage("en");
            }else{
                LanguageManager.setLanguage("de");
            }
            updateLanguage();
        });
        updateLanguage();
    }
}

package de.srh_2551.cinema_reservations.view;

import de.srh_2551.cinema_reservations.model.Basket;
import de.srh_2551.cinema_reservations.model.Seat;
import de.srh_2551.cinema_reservations.util.LanguageManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.function.Consumer;

public class BasketItemCell extends ListCell<Seat> {
    private final Basket currentBasket;
    private final Consumer<Seat> onRemoveAction; // Allows the Controller to tell us what to do on click

    public BasketItemCell(Basket currentBasket, Consumer<Seat> onRemoveAction) {
        this.currentBasket = currentBasket;
        this.onRemoveAction = onRemoveAction;
    }

    @Override
    protected void updateItem(Seat seat, boolean empty) {
        super.updateItem(seat, empty);

        if(empty || seat == null) {
            setText(null);
            setGraphic(null);
        }else{
            //item container
            HBox container = new HBox(15);
            container.setAlignment(Pos.CENTER_LEFT);
            container.getStyleClass().add("basketItemContainer");

            //seat identifier
            Label basketItem = new Label(LanguageManager.getString("basket.row") + " " + currentBasket.getRowIdentifier() + " | " + LanguageManager.getString("basket.seat") + " " + seat.getSeatNumber());
            basketItem.getStyleClass().add("basketItem");

            //seat type
            Label typeLabel = new Label(LanguageManager.getString("basket.type") + ": " + seat.getSeatType().name());
            typeLabel.getStyleClass().add("basketItem-type");

            //spacer
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            //price
            Label priceLabel = new Label(String.format("%.2f €", seat.getSeatType().getPrice()));
            priceLabel.getStyleClass().add("basketItem-price");

            //remove button
            Button removeBtn = new Button();
            removeBtn.getStyleClass().add("basketItem-remove");
            removeBtn.setDisable(currentBasket.removalNotAllowed(seat));

            removeBtn.setOnAction(ignored -> onRemoveAction.accept(seat)); // Triggers the callback!

            //assemble container
            container.getChildren().addAll(basketItem, typeLabel, spacer, priceLabel, removeBtn);
            setGraphic(container);
            setText(null);
        }
    }
}

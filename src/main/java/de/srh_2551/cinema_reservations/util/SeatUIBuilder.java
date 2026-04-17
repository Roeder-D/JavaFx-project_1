package de.srh_2551.cinema_reservations.util;

import de.srh_2551.cinema_reservations.model.Hall;
import de.srh_2551.cinema_reservations.model.Row;
import de.srh_2551.cinema_reservations.model.Seat;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Map;
import java.util.function.Consumer;

public class SeatUIBuilder {
    public static void buildSeatGrid(Hall hall, VBox container, Map<Seat, Button> seatButtonMap, Consumer<Seat> onSeatClick){
        container.getChildren().clear();
        seatButtonMap.clear();

        //add legend and screen
        container.getChildren().add(createLegend(hall));
        container.getChildren().add(createScreen(hall));

        int rowCount = 0;
        for(Row row : hall.getRows()){
            String rowName = row.getRowIdentifier();

            //add row gap
            if(row.getGapInFront()){
                HBox gapBox = new HBox();
                gapBox.prefHeight(10);
                container.getChildren().add(gapBox);
            }

            HBox rowBox = new HBox(5);
            rowBox.setAlignment(Pos.CENTER);

            if(rowCount % 2 == 0){
                rowBox.setTranslateX(25);
            }
            //Builds buttons and maps the to the controller
            for(Seat seat : row.getSeats()){
                Button seatBtn = createSeatButton(rowName, seat, onSeatClick);
                seatButtonMap.put(seat, seatBtn);
                rowBox.getChildren().add(seatBtn);
            }
            rowCount++;
            container.getChildren().add(rowBox);
        }
    }

    private static Button createSeatButton(String rowName, Seat seat, Consumer<Seat> onClick){
        Button seatBtn = new Button(rowName + " " +seat.getSeatNumber());
        seatBtn.setPrefSize(50,50);

        applySeatStyle(seatBtn, seat);

        //trigger logic in controller when clicked
        seatBtn.setOnAction(ignore -> onClick.accept(seat));

        return seatBtn;
    }

    public static void applySeatStyle(Button seatBtn, Seat seat){
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
        }
    }

    private static HBox createLegend(Hall currentHall){
        HBox legend = new HBox(20);
        legend.setAlignment(Pos.CENTER);
        legend.getStyleClass().add("legend");

        legend.getChildren().addAll(
                createLegendItem(LanguageManager.getString("legend.standard"), "legend-standard"),
                createLegendItem(LanguageManager.getString("legend.premium"), "legend-premium"),
                createLegendItem(LanguageManager.getString("legend.deluxe"), "legend-deluxe")
        );
        if(currentHall.containsSeatStatus(Seat.SeatStatus.BOOKED)){
            legend.getChildren().add(createLegendItem(LanguageManager.getString("legend.booked"), "legend-booked"));
        }
        if(currentHall.containsSeatStatus(Seat.SeatStatus.OUT_OF_ORDER)){
            legend.getChildren().add(createLegendItem(LanguageManager.getString("legend.out_of_order"), "legend-out_of_order"));
        }

        return legend;
    }

    private static HBox createLegendItem(String labelText, String cssIdentifier){
        HBox legendItem = new HBox(5);
        legendItem.setAlignment(Pos.CENTER_LEFT);

        Region colorBox = new Region();
        colorBox.getStyleClass().addAll("legend-colorBox", cssIdentifier);

        Label label = new Label(labelText);
        label.getStyleClass().add("legend-label");

        legendItem.getChildren().addAll(colorBox, label);
        return legendItem;
    }

    private static StackPane createScreen(Hall hall){
        StackPane screenContainer = new StackPane();
        screenContainer.getStyleClass().add("screenContainer");

        Label screen = new Label(LanguageManager.getString("screen.label"));
        screen.getStyleClass().add("cinema-screen");
        screen.setAlignment(Pos.CENTER);

        if(!hall.getRows().isEmpty()){
            Row frontRow = hall.getRows().getFirst();
            int seatCount = frontRow.getSeats().size();
            double screenWidth = (seatCount * 50) + ((seatCount - 1) * 5) + 100;
            screen.setPrefWidth(screenWidth);
            screen.setMinWidth(screenWidth);
        }

        screenContainer.getChildren().add(screen);
        return screenContainer;
    }
}

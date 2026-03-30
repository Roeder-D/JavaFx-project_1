package de.srh_2551.cinema_reservations.model;

import java.util.ArrayList;
import java.util.List;

public class Basket {
    private static int nextBasketId = 0;

    private int basketId;
    private List<Seat> selectedSeats;
    private double price;


    //constructor
    public Basket() {
        this.basketId = nextBasketId++;
        this.selectedSeats = new ArrayList<>();
        this.price = 0;
    }

    //getter & setter
    public int getBasketId() {
        return basketId;
    }

    public List<Seat> getSelectedSeats() {
        return selectedSeats;
    }

    public double getPrice() {
        return selectedSeats.stream()
                .mapToDouble(seat-> seat.getSeatType().getPrice())
                .sum();
    }


}

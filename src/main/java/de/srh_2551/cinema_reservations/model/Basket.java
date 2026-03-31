package de.srh_2551.cinema_reservations.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Basket {
    private static final AtomicInteger nextBasketId = new AtomicInteger(1);

    private final int basketId;
    private List<Seat> selectedSeats;

    //constructor
    public Basket() {
        this.basketId = nextBasketId.getAndIncrement();
        this.selectedSeats = new ArrayList<>();
    }

    //getter & setter
    public int getBasketId() {
        return basketId;
    }

    public List<Seat> getSelectedSeats() {
        return java.util.Collections.unmodifiableList(selectedSeats);
    }

    public double getPrice() {
        return selectedSeats.stream()
                .mapToDouble(seat-> seat.getSeatType().getPrice())
                .sum();
    }

    public void addSeat(Seat seat){
        if(seat != null && !selectedSeats.contains(seat) && seat.getSeatStatus() ==  Seat.SeatStatus.FREE){
            selectedSeats.add(seat);
            seat.setSeatStatus(Seat.SeatStatus.SELECTED);
        }else{
            throw new IllegalStateException("Seat is not available!");
        }
    }

    public void removeSeat(Seat seat){
        if(selectedSeats.contains(seat)){
            selectedSeats.remove(seat);
            seat.setSeatStatus(Seat.SeatStatus.FREE);
        }
    }

    //helper
    public boolean confirmOrder(){
        for(Seat seat : selectedSeats){
            if(seat.getSeatStatus().equals(Seat.SeatStatus.SELECTED)){
                seat.setSeatStatus(Seat.SeatStatus.BOOKED);
            }
        }
        return true;
    }

}

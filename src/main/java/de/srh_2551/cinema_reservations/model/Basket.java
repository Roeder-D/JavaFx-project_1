package de.srh_2551.cinema_reservations.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Basket {
    private static final AtomicInteger nextBasketId = new AtomicInteger(1);

    private final int basketId;
    private final List<Seat> selectedSeats;
    private final Hall selectedHall;

    //constructor
    public Basket(Hall selectedHall) {
        this.basketId = nextBasketId.getAndIncrement();
        this.selectedSeats = new ArrayList<>();
        this.selectedHall = selectedHall;
    }

    //getter & setter
    public int getBasketId() {
        return basketId;
    }

    public List<Seat> getSelectedSeats() {

        return java.util.Collections.unmodifiableList(selectedSeats);
    }

    public String getRowIdentifier() {
        if(!selectedSeats.isEmpty()) {
            return selectedHall.getRow(selectedSeats.getFirst().getRowId()).getRowIdentifier();
        }else{
            return "Error";
        }
    }

    public double getPrice() {
        return selectedSeats.stream()
                .mapToDouble(seat-> seat.getSeatType().getPrice())
                .sum();
    }

    public void addSeat(Seat seat){
        if(seatIsAdjacent(seat)) {
            if (seat != null && !selectedSeats.contains(seat) && seat.getSeatStatus() == Seat.SeatStatus.FREE) {
                selectedSeats.add(seat);
                seat.setSeatStatus(Seat.SeatStatus.SELECTED);
            } else {
                throw new IllegalStateException("Seat is not available!");
            }
        }else{
            throw new IllegalStateException("Seats must be adjacent!");
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

    public boolean seatIsAdjacent(Seat selectedSeat){
        //TODO: Look for booked seats in row (min gap 2)?
        if(!selectedSeats.isEmpty()) {
            for (Seat seat : selectedSeats) {
                if (selectedSeat.getRowId() == (seat.getRowId())) {
                    if (selectedSeat.getSeatNumber() == (seat.getSeatNumber() - 1) || selectedSeat.getSeatNumber() == (seat.getSeatNumber() + 1)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }

}

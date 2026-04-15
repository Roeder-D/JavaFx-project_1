package de.srh_2551.cinema_reservations.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Basket {
    private static final AtomicInteger nextBasketId = new AtomicInteger(1);

    private final int basketId;
    private final List<Seat> selectedSeats;
    private final Hall selectedHall;
    private Discounts currentDiscount = Discounts.DEFAULT;

    public enum Discounts{
        DEFAULT(0),
        STUDENT(0.10),
        ELDERLY(0.15),
        DISABLED(0.30);

        private final double discount;

        Discounts(double discount){
            this.discount = discount;
        }
        public double getDiscount() {
            return discount;
        }
    }

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

    public BigDecimal getPrice() {
        double price = selectedSeats.stream()
                .mapToDouble(seat-> seat.getSeatType().getPrice())
                .sum();

        price *= (1-currentDiscount.getDiscount());

        return BigDecimal.valueOf(price).setScale(2, RoundingMode.DOWN);
    }

    public Hall getSelectedHall() {
        return selectedHall;
    }

    public void setCurrentDiscount(Discounts newDiscount) {
        this.currentDiscount = newDiscount;
    }

    public void addSeat(Seat seat){
        //check for null
        if (seat == null) {
            throw new IllegalStateException("Seat is not available!");
        }
        //check for adjacent
        if(!seatIsAdjacent(seat)) {
                throw new IllegalStateException("Seats must be adjacent!");
            }

        //check for new gaps
        if(createsIllegalGap(seat)){
        throw new IllegalStateException("Seat can't create a single gap");
        }

        //add seat
        selectedSeats.add(seat);
        seat.setSeatStatus(Seat.SeatStatus.SELECTED);
    }

    public void removeSeat(Seat seat){
        //check for existence
        if(!selectedSeats.contains(seat)) {
            throw new IllegalStateException("Seat does not exist!");
        }
        //prevent center selection
        else{
            //prevent new gaps
            if(removalNotAllowed(seat)) {
                throw new IllegalStateException("Cannot create a single seat gap");
            }

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
        selectedSeats.clear();
        return true;
    }

    //helper

    public void cancelOrder(){
        for(Seat seat : selectedSeats){
            seat.setSeatStatus(Seat.SeatStatus.FREE);
        }
        selectedSeats.clear();
    }

    public boolean seatIsAdjacent(Seat selectedSeat){
        //first seat in selection skips this
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

    public boolean createsIllegalGap(Seat selectedSeat){
        Row selectedRow = getSelectedHall().getRow(selectedSeat.getRowId());
        int seatNmb = selectedSeat.getSeatNumber();

        Seat.SeatStatus seatMinus1 = null;
        Seat.SeatStatus seatPlus1 = null;
        Seat.SeatStatus seatMinus2  = null;
        Seat.SeatStatus seatPlus2 = null;

        if(selectedRow.getSeatByNumber(seatNmb - 1) != null){
            seatMinus1 = selectedRow.getSeatByNumber(seatNmb - 1).getSeatStatus();
        }
        if(selectedRow.getSeatByNumber(seatNmb + 1) != null){
            seatPlus1 = selectedRow.getSeatByNumber(seatNmb + 1).getSeatStatus();
        }
        if(selectedRow.getSeatByNumber(seatNmb - 2) != null){
            seatMinus2 = selectedRow.getSeatByNumber(seatNmb - 2).getSeatStatus();
        }
        if(selectedRow.getSeatByNumber(seatNmb + 2) != null){
            seatPlus2 = selectedRow.getSeatByNumber(seatNmb + 2).getSeatStatus();
        }

        boolean createsLeftGap = false;
        boolean createsRightGap = false;

        if(seatMinus1 == Seat.SeatStatus.FREE){
            if(seatMinus2 !=  Seat.SeatStatus.FREE){
                createsLeftGap = true;
            }
        }
        if(seatPlus1 == Seat.SeatStatus.FREE){
            if(seatPlus2 !=  Seat.SeatStatus.FREE){
                createsRightGap = true;
            }
        }
        //block if creates a single seat gap
        return createsLeftGap || createsRightGap;
    }

    public boolean removalNotAllowed(Seat selectedSeat){
        Row selectedRow = getSelectedHall().getRow(selectedSeat.getRowId());
        int selectedId = selectedSeat.getSeatNumber();
        //check for middle seat
        int matches = 0;

        for(Seat seat : selectedSeats) {
            if(seat.getSeatNumber() == selectedId + 1 || seat.getSeatNumber() == selectedId - 1) {
                matches++;
            }
        }
        if(matches >= 2){
            return true;
        }
        //check for full block
        boolean minFree = false;
        boolean maxFree = false;
        int min = selectedRow.getSeatCount();
        int max = 0;
        for(Seat seat : selectedSeats){
            int seatId = seat.getSeatNumber();
            if(seatId < min){
                min = seatId;
            }
            if(seatId > max){
                max = seatId;
            }
        }
        if(selectedRow.getSeatByNumber(min-1) != null &&  selectedRow.getSeatByNumber(min-1).getSeatStatus() == Seat.SeatStatus.FREE){
            minFree = true;
        }
        if(selectedRow.getSeatByNumber(max+1) != null && selectedRow.getSeatByNumber( max+1).getSeatStatus() == Seat.SeatStatus.FREE){
            maxFree = true;
        }
        //allow removal if only one seat
        if(min == max){
            return false;
        }
        //allow removal if both ends are free/blocked
        if(minFree == maxFree){
            return false;
        }
        //forbid removal when there only is a seat at the other end
        if (minFree) {
            return min != selectedId;
        }else {
        return max != selectedId;
        }
    }
}

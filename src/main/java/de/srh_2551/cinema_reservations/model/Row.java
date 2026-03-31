package de.srh_2551.cinema_reservations.model;

import java.util.ArrayList;
import java.util.List;

public class Row {
    private String rowIdentifier;
    private final int rowId;
    private int seatCount;
    private List<Seat> seats;

    //constructor
    public Row(String rowName, int rowId, int seatCount, Seat.SeatType seatType) {
        if (verifyName(rowName) && seatCount > 0) {
            this.rowIdentifier = rowName;
            this.rowId = rowId;
            this.seatCount = seatCount;
            this.initializeSeats(seatType);
        } else {
            throw new IllegalArgumentException("Invalid name or  seat count!");
        }
    }

    //verifier
    private boolean verifyName(String name) {
        return (name != null && name.matches("^[\\p{L}\\p{N}_ -]+$"));
    }

    public boolean verifySeatNumber(int seatNumber) {
        return (seatNumber > 0 && seatNumber <= this.seatCount);
    }


    //setup
    private void initializeSeats(Seat.SeatType seatType) {
        this.seats = new ArrayList<>();
        for (int i = 1; i <= seatCount; i++) {
            Seat newSeat = new Seat(i, rowId, seatType, Seat.SeatStatus.FREE);
            this.seats.add(newSeat);
        }
    }

    //getter & setter
    public String getRowIdentifier() {
        return rowIdentifier;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    //helper
    public Seat getSeatByNumber(int seatNumber) {
        if (verifySeatNumber(seatNumber)) {
            Seat selectedSeat = this.seats.get(seatNumber-1);
            if(selectedSeat.getSeatNumber() == seatNumber) {
                return selectedSeat;
            }else{
                throw new IllegalStateException("Internal error: index out of sync!");
            }
        }
        return null;
    }

    public List<Seat> getSeatsByStatus(Seat.SeatStatus seatStatus) {
        List<Seat> selectedSeats = new ArrayList<>();
        for (Seat seat : this.seats) {
            if (seat.getSeatStatus().equals(seatStatus)) {
                selectedSeats.add(seat);
            }
        }
        return java.util.Collections.unmodifiableList(selectedSeats);
    }
}
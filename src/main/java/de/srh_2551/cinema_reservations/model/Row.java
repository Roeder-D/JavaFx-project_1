package de.srh_2551.cinema_reservations.model;


import java.util.ArrayList;
import java.util.List;

public class Row {
    private final String rowIdentifier;
    private final int rowId;
    private final int seatCount;
    private final List<Seat> seats;
    private final boolean gapInFront;

    //constructor
    public Row(String rowName, int rowId, int seatCount, Seat.SeatType seatType, boolean gapInFront) {
        if (verifyName(rowName) && seatCount > 0) {
            this.rowIdentifier = rowName;
            this.rowId = rowId;
            this.seatCount = seatCount;
            this.seats = initializeSeats(seatType);
            this.gapInFront = gapInFront;
        } else {
            throw new IllegalArgumentException("Invalid name or seat count!");
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
    private List<Seat> initializeSeats(Seat.SeatType seatType) {
        List<Seat> seatList = new ArrayList<>();
        for (int i = 1; i <= seatCount; i++) {
            Seat newSeat = new Seat(i, rowId, seatType, Seat.SeatStatus.FREE);
            seatList.add(newSeat);
        }
        return seatList;
    }

    //getter & setter
    public String getRowIdentifier() {
        return rowIdentifier;
    }

    public List<Seat> getSeats() {
        return java.util.Collections.unmodifiableList(seats);
    }

    public int getSeatCount() {
        return seatCount;
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

    public int getRowId() {
        return this.rowId;
    }

    public boolean getGapInFront() {
        return this.gapInFront;
    }
}
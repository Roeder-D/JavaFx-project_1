package de.srh_2551.cinema_reservations.model;

public class Seat {
    private final int seatNumber;
    private final int rowId;              //for GUI
    private SeatType seatType;
    private SeatStatus seatStatus;

    public enum SeatType{
        STANDARD(10.0),
        PREMIUM(15.0),
        DELUXE(20.0);

        private final double price;
        SeatType(double price){
            this.price = price;
        }
        public double getPrice(){
            return price;
        }

    }

    public enum SeatStatus{
        OUT_OF_ORDER,
        FREE,
        SELECTED,
        RESERVED,
        BOOKED
    }

    //constructor
    public Seat(int seatNumber, int rowId, SeatType seatType, SeatStatus seatStatus) {
        if (verifySeatNumber(seatNumber)) {
            this.seatNumber = seatNumber;
            this.rowId = rowId;
            this.seatType = seatType;
            this.seatStatus = seatStatus;
        }else {
            throw new IllegalArgumentException("Illegal seat number");
        }
    }

    //verifier
    private boolean verifySeatNumber(int seatNumber){
        return (seatNumber >= 1);
    }

    //getter & setter
    public int getSeatNumber() {
        return seatNumber;
    }

    public SeatType getSeatType() {
        return seatType;
    }
    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }

    public SeatStatus getSeatStatus() {
        return seatStatus;
    }
    public void setSeatStatus(SeatStatus seatStatus) {
        this.seatStatus = seatStatus;
    }

    public int getRowId() {
        return rowId;
    }
}

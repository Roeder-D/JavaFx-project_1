package de.srh_2551.cinema_reservations.model;

import java.util.ArrayList;
import java.util.List;

public class Hall {
    private String name;
    private List<Row> rows;

    //constructor
    public Hall(String name) {
        if(verifyName(name)) {
            this.name = name;
            this.rows = new ArrayList<Row>();
        }else {
            throw new IllegalArgumentException("Invalid name!");
        }
    }

    //verifier
    private boolean verifyName(String name) {
        return (name != null && name.matches("^[\\p{L}\\p{N}_ -]+$"));
    }

    private boolean rowExists(String rowName) {
        for (Row row: this.rows){
            if(row.getRowIdentifier().equals(rowName)) {
                return true;
            }
        }
        return false;
    }

    //setup
    public void addRow(Row row) {
        if(row != null && !rowExists(row.getRowIdentifier())) {
            this.rows.add(row);
        }else{
            throw new IllegalArgumentException("Already exists or null!");
        }
    }

    //getter & setter
    public String getName() {
        return name;
    }
    public void setName(String name) {
        if(verifyName(name)){
            this.name = name;
        }else  {
            throw new IllegalArgumentException("Invalid name!");
        }
    }

    public List<Row> getRows() {
        return rows;
    }


    public Seat getSeat(int seatNumber, String rowName) {
        for (Row row : this.rows) {
            if(row.getRowIdentifier().equals(rowName)) {
                if(row.verifySeatNumber(seatNumber)) {
                    return row.getSeatByNumber(seatNumber);
                }else{
                    return null;//seat not found
                }
            }
        }
        return null;//row not found
    }

    public List<Seat> getSeats() {
        List<Seat> seats = new ArrayList<>();
        for (Row row : this.rows) {
            seats.addAll(row.getSeats());
        }
        return seats;
    }


    //helper
    public List<Seat> getSeatsByStatus(Seat.SeatStatus seatStatus) {
        List<Seat> seats = new ArrayList<>();
        for (Row row : this.rows) {
            row.getSeatsByStatus(seatStatus);
            seats.addAll(row.getSeats());
        }
        return java.util.Collections.unmodifiableList(seats);
    }
}

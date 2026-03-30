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

    //setup
    public void addRow(Row row) {
        if(row != null) {
            this.rows.add(row);
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

    public Seat getSeat(int seatNumber, String rowName) {
        for (Row row : this.rows) {
            if(row.getRowIdentifier().equals(rowName) && row.verifySeatNumber(seatNumber)) {
                return row.getSeatByNumber(seatNumber);
            }
        }
        return null;
    }

    public List<Seat> getSeats() {
        List<Seat> seats = new ArrayList<>();
        for (Row row : this.rows) {
            seats.addAll(row.getSeats());
        }
        return seats;
    }


}

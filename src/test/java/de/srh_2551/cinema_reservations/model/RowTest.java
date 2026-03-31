package de.srh_2551.cinema_reservations.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RowTest {

    @Test
    void testValidRowCreation() {
        Row row = new Row("Row A", 1, 10, Seat.SeatType.STANDARD);

        assertEquals("Row A", row.getRowIdentifier());
        assertEquals(10, row.getSeats().size(), "Row should initialize exactly 10 seats");
    }

    @Test
    void testInvalidRowCreationThrowsException() {
        // Test invalid name
        assertThrows(IllegalArgumentException.class,
                () -> new Row("Invalid@Name!", 1, 10, Seat.SeatType.STANDARD));

        // Test invalid seat count (0 or negative)
        assertThrows(IllegalArgumentException.class,
                () -> new Row("Row B", 2, 0, Seat.SeatType.STANDARD));
    }

    @Test
    void testGetSeatByNumber() {
        Row row = new Row("Row A", 1, 10, Seat.SeatType.STANDARD);

        // Valid seat
        Seat seat5 = row.getSeatByNumber(5);
        assertNotNull(seat5);
        assertEquals(5, seat5.getSeatNumber());

        // Invalid seats (out of bounds)
        assertNull(row.getSeatByNumber(0), "Seat 0 should not exist");
        assertNull(row.getSeatByNumber(11), "Seat 11 should not exist in a 10-seat row");
    }
}
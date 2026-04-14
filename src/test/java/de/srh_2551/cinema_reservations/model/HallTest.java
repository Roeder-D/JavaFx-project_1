package de.srh_2551.cinema_reservations.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HallTest {

    @Test
    void testValidHallCreation() {
        Hall hall = new Hall("Main Hall");
        assertEquals("Main Hall", hall.getName());
        assertTrue(hall.getSeats().isEmpty(), "New hall should have no seats yet");
    }

    @Test
    void testInvalidHallNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Hall(""));
        assertThrows(IllegalArgumentException.class, () -> new Hall(null));
    }

    @Test
    void testAddRowAndGetSeat() {
        Hall hall = new Hall("Main Hall");
        Row rowA = new Row("A", 1, 5, Seat.SeatType.PREMIUM, false);
        Row rowB = new Row("B", 2, 5, Seat.SeatType.STANDARD, false);

        hall.addRow(rowA);
        hall.addRow(rowB);

        // Fetch a valid seat
        Seat fetchedSeat = hall.getSeat(3, "B");
        assertNotNull(fetchedSeat);
        assertEquals(3, fetchedSeat.getSeatNumber());
        assertEquals(Seat.SeatType.STANDARD, fetchedSeat.getSeatType());

        // Try to fetch a seat from a row that doesn't exist
        assertNull(hall.getSeat(1, "C"));

        // Try to fetch a seat number that doesn't exist in a valid row
        assertNull(hall.getSeat(10, "A"));
    }

    @Test
    void testDuplicateRowThrowsException() {
        Hall hall = new Hall("Main Hall");
        Row rowA = new Row("A", 1, 5, Seat.SeatType.PREMIUM, false);

        hall.addRow(rowA);

        assertThrows(IllegalArgumentException.class, () -> hall.addRow(rowA));
    }
}
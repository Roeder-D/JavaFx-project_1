package de.srh_2551.cinema_reservations.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeatTest {

    @Test
    void testValidSeatCreation() {
        // Arrange & Act
        Seat seat = new Seat(1, 5, Seat.SeatType.STANDARD, Seat.SeatStatus.FREE);

        // Assert
        assertEquals(1, seat.getSeatNumber(), "Seat number should be 1");
        assertEquals(5, seat.getRowId(), "Row ID should be 5");
        assertEquals(Seat.SeatType.STANDARD, seat.getSeatType(), "Seat type should be STANDARD");
        assertEquals(Seat.SeatStatus.FREE, seat.getSeatStatus(), "Seat status should be FREE");
    }

    @Test
    void testInvalidSeatCreationThrowsException() {
        // Assert that creating a seat with number 0 throws the exception
        IllegalArgumentException thrown0 = assertThrows(
                IllegalArgumentException.class,
                () -> new Seat(0, 5, Seat.SeatType.STANDARD, Seat.SeatStatus.FREE),
                "Expected Seat() to throw exception, but it didn't"
        );
        assertEquals("Illegal seat number", thrown0.getMessage());

        // Assert that creating a seat with a negative number throws the exception
        assertThrows(
                IllegalArgumentException.class,
                () -> new Seat(-5, 5, Seat.SeatType.STANDARD, Seat.SeatStatus.FREE)
        );
    }

    @Test
    void testSettersChangeState() {
        // Arrange
        Seat seat = new Seat(1, 5, Seat.SeatType.STANDARD, Seat.SeatStatus.FREE);

        // Act
        seat.setSeatType(Seat.SeatType.PREMIUM);
        seat.setSeatStatus(Seat.SeatStatus.BOOKED);

        // Assert
        assertEquals(Seat.SeatType.PREMIUM, seat.getSeatType(), "Type should update to PREMIUM");
        assertEquals(Seat.SeatStatus.BOOKED, seat.getSeatStatus(), "Status should update to BOOKED");
    }
}
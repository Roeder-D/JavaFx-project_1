package de.srh_2551.cinema_reservations.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasketTest {

    private Basket basket;
    private Seat standardSeat;
    private Seat premiumSeat;

    @BeforeEach
    void setUp() {
        basket = new Basket();
        standardSeat = new Seat(1, 1, Seat.SeatType.STANDARD, Seat.SeatStatus.FREE);
        premiumSeat = new Seat(2, 1, Seat.SeatType.PREMIUM, Seat.SeatStatus.FREE);
    }

    @Test
    void testAddSeatChangesStatusAndCalculatesPrice() {
        basket.addSeat(standardSeat);
        basket.addSeat(premiumSeat);

        assertEquals(2, basket.getSelectedSeats().size());
        assertEquals(Seat.SeatStatus.SELECTED, standardSeat.getSeatStatus(), "Status should change to SELECTED when added");

        // 10.0 (Standard) + 15.0 (Premium) = 25.0
        assertEquals(25.0, basket.getPrice(), "Price calculation is incorrect");
    }

    @Test
    void testRemoveSeatRevertsStatus() {
        basket.addSeat(standardSeat);
        basket.removeSeat(standardSeat);

        assertTrue(basket.getSelectedSeats().isEmpty());
        assertEquals(Seat.SeatStatus.FREE, standardSeat.getSeatStatus(), "Status should revert to FREE when removed");
        assertEquals(0.0, basket.getPrice());
    }

    @Test
    void testConfirmOrderChangesStatusToBooked() {
        basket.addSeat(standardSeat);
        basket.addSeat(premiumSeat);

        boolean isConfirmed = basket.confirmOrder();

        assertTrue(isConfirmed);
        assertEquals(Seat.SeatStatus.BOOKED, standardSeat.getSeatStatus(), "Status should be BOOKED after confirmation");
        assertEquals(Seat.SeatStatus.BOOKED, premiumSeat.getSeatStatus(), "Status should be BOOKED after confirmation");
    }

    @Test
    void testBasketIdItterates(){
        Basket basket2 = new Basket();
        assertEquals(basket.getBasketId()+1, basket2.getBasketId(), "BasketId should increment by 1");
    }

    @Test
    void testPriceDrop(){
        basket.addSeat(standardSeat);
        basket.addSeat(premiumSeat);

        basket.removeSeat(premiumSeat);

        assertEquals(10.0, basket.getPrice(), "Price shoul decrease after removal");
    }

    @Test
    void testSeatNotFree(){
        basket.addSeat(standardSeat);

        assertThrows(IllegalStateException.class, () -> {basket.addSeat(standardSeat);});
    }
}
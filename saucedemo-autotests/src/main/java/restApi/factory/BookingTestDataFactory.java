package restApi.factory;

import restApi.pojo.bookers.Booking;
import restApi.pojo.bookers.BookingDates;

/**
 * Фабрика для создания тестовых данных бронирований.
 * Реализует паттерн Builder для гибкого создания объектов Booking.
 *
 * @author Chikanov Anton
 * @see Booking
 * @see BookingDates
 */
public class BookingTestDataFactory {

    public static Booking.BookingBuilder aBooking() {
        return Booking.builder();
    }

    public static Booking standardBooking() {
        return Booking.builder()
                .firstName("John")
                .lastName("Smith")
                .totalPrice(1000)
                .depositPaid(true)
                .bookingDates(new BookingDates("2025-01-01", "2025-01-10"))
                .additionalNeeds("Breakfast")
                .build();
    }
}

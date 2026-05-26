package apiTests.booker;

import org.testng.annotations.Test;
import restApi.client.base.ApiClients;
import restApi.client.BookingApiClient;
import restApi.factory.BookingTestDataFactory;
import restApi.pojo.bookers.Booking;
import restApi.pojo.bookers.BookingDates;
import restApi.pojo.bookers.BookingId;
import restApi.utils.EnvironmentManager;

import java.util.List;

public class TestBookerWithPojo  {

    private BookingApiClient bookingClient = ApiClients.booking();

    @Test
    public void testCreateBookingApi() {

        Booking booking = BookingTestDataFactory.aBooking()
                .firstName("Jane")
                .lastName("Doe")
                .totalPrice(2000)
                .depositPaid(true)
                .bookingDates(new BookingDates("2025-01-01", "2025-01-10"))
                .additionalNeeds("la-la")
                .build();

        var response = bookingClient.createBooking(booking);
        System.out.println("Created booking: " + response.getBookingId());

        // Получаем созданное бронирование
        Booking retrieved = bookingClient.getBookingById(response.getBookingId());
        System.out.println("Retrieved: " + retrieved.getFirstName());
    }

    @Test
    public void testGetBookingApi() {
        // Получаем созданное бронирование
        List<BookingId> retrieved = bookingClient.getAllBookingIds();
        System.out.println("Retrieved: " + retrieved);
    }

    @Test
    public void testBookingApi() {

        Booking booking = BookingTestDataFactory.aBooking()
                .firstName("Jane")
                .lastName("Doe")
                .totalPrice(2000)
                .depositPaid(true)
                .bookingDates(new BookingDates("2025-01-01", "2025-01-10"))
                .additionalNeeds("la-la")
                .build();

        var response = bookingClient.createBooking(booking);
        System.out.println("Created booking: " + response.getBookingId());
    }

    @Test
    public void testEnvironment() {
        System.out.println("=== ENVIRONMENT CHECK ===");
        System.out.println("VM option -Denv = " + System.getProperty("env"));
        System.out.println("Current env from manager: " + EnvironmentManager.getCurrentEnvironment());
        System.out.println("Is DEV: " + EnvironmentManager.isDev());
        System.out.println("Is IFT: " + EnvironmentManager.isIft());
        System.out.println("Booker URL: " + EnvironmentManager.getConfig().apiBookerUrl());
        System.out.println("=========================");
    }
}
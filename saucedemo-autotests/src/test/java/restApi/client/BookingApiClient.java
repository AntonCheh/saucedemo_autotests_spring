package restApi.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import restApi.manager.PathManager;
import restApi.pojo.bookers.Booking;
import restApi.pojo.bookers.BookingId;
import restApi.pojo.bookers.BookingResponse;
import restApi.properties.BookingPathsConfig;

import java.util.List;

import static io.restassured.RestAssured.given;

/**
 * HTTP клиент для взаимодействия с Restful-Booker API.
 * Инкапсулирует все CRUD операции над бронированиями.
 */
public class BookingApiClient {
    private final RequestSpecification requestSpec;
    private final BookingPathsConfig paths;

    public BookingApiClient(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
        this.paths = PathManager.getBookingPaths(); // ← загружаем пути
    }

    // GET /booking - получить все ID
    public List<BookingId> getAllBookingIds() {
        return given()
                .spec(requestSpec)
                .when()
                .get(paths.getAllBookings()) // ← из конфига
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", BookingId.class);
    }

    // GET /booking/{id} - получить бронирование по ID
    public Booking getBookingById(int bookingId) {
        String endpoint = paths.getBooking().replace("{id}", String.valueOf(bookingId));
        return given()
                .spec(requestSpec)
                .when()
                .get(endpoint)
                .then()
                .statusCode(200)
                .extract()
                .as(Booking.class);
    }

    // POST /booking - создать бронирование
    public BookingResponse createBooking(Booking booking) {
        return given()
                .spec(requestSpec)
                .body(booking)
                .when()
                .post(paths.createBooking())
                .then()
                .statusCode(200)
                .extract()
                .as(BookingResponse.class);
    }

    // PUT /booking/{id} - обновить бронирование
    public Response updateBooking(int bookingId, Booking booking, String token) {
        String endpoint = paths.updateBooking().replace("{id}", String.valueOf(bookingId));
        return given()
                .spec(requestSpec)
                .header("Cookie", "token=" + token)
                .body(booking)
                .when()
                .put(endpoint)
                .then()
                .extract()
                .response();
    }

    // DELETE /booking/{id} - удалить бронирование
    public Response deleteBooking(int bookingId, String token) {
        String endpoint = paths.deleteBooking().replace("{id}", String.valueOf(bookingId));
        return given()
                .spec(requestSpec)
                .header("Cookie", "token=" + token)
                .when()
                .delete(endpoint)
                .then()
                .extract()
                .response();
    }
}
package restApi.pojo.bookers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * POJO класс для десериализации ответа API при создании бронирования.
 * API возвращает созданный объект бронирования вместе с присвоенным ID.
 *
 * Пример ответа:
 * {
 *   "bookingid": 123,
 *   "booking": { ... }
 * }
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingResponse {

    @JsonProperty("bookingid")
    private Integer bookingId;

    private Booking booking;

    @Override
    public String toString() {
        return String.format("BookingResponse{bookingId=%d, booking=%s}", bookingId, booking);
    }
}

package restApi.pojo.bookers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * POJO класс для десериализации списка ID бронирований.
 * API возвращает массив объектов, каждый из которых содержит только bookingid.
 *
 * Пример ответа: [{"bookingid": 1}, {"bookingid": 2}, ...]
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingId {

    @JsonProperty("bookingid")
    private Integer bookingId;

    @Override
    public String toString() {
        return "BookingId{bookingId=" + bookingId + "}";
    }
}
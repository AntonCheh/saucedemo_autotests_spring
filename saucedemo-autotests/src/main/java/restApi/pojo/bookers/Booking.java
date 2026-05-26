package restApi.pojo.bookers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO класс, представляющий бронирование в системе Restful-Booker.
 * Используется для сериализации запросов и десериализации ответов API.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Booking {

    @JsonProperty("firstname")
    private String firstName;

    @JsonProperty("lastname")
    private String lastName;

    @JsonProperty("totalprice")
    private Integer totalPrice;

    @JsonProperty("depositpaid")
    private Boolean depositPaid;

    @JsonProperty("bookingdates")
    private BookingDates bookingDates;

    @JsonProperty("additionalneeds")
    private String additionalNeeds;

    @Override
    public String toString() {
        return String.format("Booking{firstname='%s', lastname='%s', totalprice=%d, depositpaid=%s, bookingdates=%s, additionalneeds='%s'}",
                firstName, lastName, totalPrice, depositPaid, bookingDates, additionalNeeds);
    }
}
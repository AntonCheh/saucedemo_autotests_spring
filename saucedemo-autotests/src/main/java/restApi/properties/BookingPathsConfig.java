package restApi.properties;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources({
        "file:src/main/resources/paths/booking-paths.properties",
        "system:properties",
        "system:env"
})
public interface BookingPathsConfig extends Config {

    @Key("booking.create")
    @DefaultValue("/booking")
    String createBooking();

    @Key("booking.get")
    @DefaultValue("/booking/{id}")
    String getBooking();

    @Key("booking.getAll")
    @DefaultValue("/booking")
    String getAllBookings();

    @Key("booking.update")
    @DefaultValue("/booking/{id}")
    String updateBooking();

    @Key("booking.delete")
    @DefaultValue("/booking/{id}")
    String deleteBooking();

    @Key("booking.partialUpdate")
    @DefaultValue("/booking/{id}")
    String partialUpdateBooking();
}

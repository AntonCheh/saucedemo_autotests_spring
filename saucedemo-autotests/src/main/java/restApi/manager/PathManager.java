package restApi.manager;

import org.aeonbits.owner.ConfigCache;
import restApi.properties.BookingPathsConfig;

public class PathManager {
    private static BookingPathsConfig bookingPaths;

    public static BookingPathsConfig getBookingPaths() {
        if (bookingPaths == null) {
            bookingPaths = ConfigCache.getOrCreate(BookingPathsConfig.class);
        }
        return bookingPaths;
    }
}
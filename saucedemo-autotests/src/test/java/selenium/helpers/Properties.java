package selenium.helpers;

import selenium.config.TestConfig;
import org.aeonbits.owner.ConfigFactory;

public class Properties {

    public static TestConfig testProperties = ConfigFactory.create(TestConfig.class);
}

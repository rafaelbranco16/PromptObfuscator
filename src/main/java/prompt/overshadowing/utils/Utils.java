package prompt.overshadowing.utils;

import java.io.IOException;
import java.util.Properties;

public class Utils {
    public static String getProperty(String key) {
        Properties prop = new Properties();
        try {
            prop.load(Utils.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return prop.getProperty(key);
    }
}

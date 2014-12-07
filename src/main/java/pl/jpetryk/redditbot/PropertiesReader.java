package pl.jpetryk.redditbot;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Jan on 06/12/14.
 */
public class PropertiesReader {


    private static final Logger logger = Logger.getLogger(PropertiesReader.class);
    public static final String DEFAULT_VALUE = "";
    /*package*/ static final String CONFIG_FOLDER = "config/";

    private Properties properties;
    private String fileName;

    /**
     * @param fileName - name of a file in config directory
     */
    public PropertiesReader(String fileName) {
        this.fileName = fileName;
        try {
            properties = openPropertyFile(fileName);
        } catch (IOException e) {
            String message = "Could not open property file: " + fileName;
            logger.error(message);
            throw new RuntimeException(message);
        }
    }

    public String getProperty(String key) {
        String result = properties.getProperty(key);
        if (result == null) {
            logger.error("Could not find property " + key + " in file " + fileName);
            return DEFAULT_VALUE;
        } else {
            return result;
        }
    }

    private Properties openPropertyFile(String fileName) throws IOException {
        InputStream inputStream = new FileInputStream(CONFIG_FOLDER + fileName);
        Properties properties = new Properties();
        properties.load(inputStream);
        inputStream.close();
        return properties;
    }
}

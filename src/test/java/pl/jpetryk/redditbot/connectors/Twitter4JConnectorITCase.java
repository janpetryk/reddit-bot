package pl.jpetryk.redditbot.connectors;

import pl.jpetryk.redditbot.utils.PropertiesReader;

/**
 * Created by Jan on 09/01/15.
 */
public class Twitter4JConnectorITCase extends AbstractTwitterConnectorITCase<Twitter4JConnector> {


    private PropertiesReader properties = new PropertiesReader("twitter.properties");

    @Override
    protected Twitter4JConnector createInstance() {
        return new Twitter4JConnector(properties.getProperty("api-key"),
                properties.getProperty("api-secret"),
                properties.getProperty("access-token"),
                properties.getProperty("access-token-secret"));
    }
}

package pl.jpetryk.redditbot.connectors;

import pl.jpetryk.redditbot.utils.PropertiesReader;

/**
 * Created by Jan on 09/01/15.
 */
public class Twitter4JConnectorITCase extends AbstractTwitterConnectorITCase<Twitter4JConnector> {


    private PropertiesReader properties = new PropertiesReader("resources/twitter.properties");

    @Override
    protected Twitter4JConnector createInstance() {
        return new Twitter4JConnector.Builder()
                .apiKey(properties.getProperty("api-key"))
                .apiSecret(properties.getProperty("api-secret"))
                .accessToken(properties.getProperty("access-token"))
                .accessTokenSecret(properties.getProperty("access-token-secret"))
                .build();
    }
}

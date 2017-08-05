package pl.jpetryk.redditbot.connectors;


import pl.jpetryk.redditbot.BotModule;

public class CustomRedditConnectorITCase extends AbstractRedditConnectorITCase<CustomRedditConnector> {


    @Override
    protected CustomRedditConnector createValidInstance() throws Exception {
        return new CustomRedditConnector(testProperties.getProperty("reddit-useragent"),
                testProperties.getProperty("reddit-login"),
                testProperties.getProperty("reddit-password"),
                testProperties.getProperty("reddit-client-id"),
                testProperties.getProperty("reddit-client-secret"),
                BotModule.okHttpClient(),
                BotModule.objectMapper());
    }

    @Override
    protected CustomRedditConnector createInvalidInstance() throws Exception {
        return new CustomRedditConnector(testProperties.getProperty("reddit-useragent"),
                testProperties.getProperty("reddit-login"),
                "INVALID_PASSWORD",
                testProperties.getProperty("reddit-client-id"),
                testProperties.getProperty("reddit-client-secret"),
                BotModule.okHttpClient(),
                BotModule.objectMapper());
    }

}

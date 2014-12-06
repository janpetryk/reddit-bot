package pl.jpetryk.redditbot.redditconnector;

import org.junit.Assert;
import org.junit.Test;
import pl.jpetryk.redditbot.PropertiesReader;

/**
 * Created by Jan on 06/12/14.
 */
public abstract class AbstractRedditConnectorITCase<T extends RedditConnectorInterface> {

    protected abstract T createInstance();

    private PropertiesReader testProperties = new PropertiesReader("testbot.properties");

    protected T connector = createInstance();

    @Test
    public void testInitialize() {
        connector.initialize(testProperties.getProperty("reddit-useragent"));
    }

    @Test
    public void testLoginWithValidCredentials() {
        String login = testProperties.getProperty("reddit-login");
        String password = testProperties.getProperty("reddit-password");
        Assert.assertTrue(connector.login(login, password));
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        String login = testProperties.getProperty("reddit-login");
        String password = "asdasd";
        Assert.assertFalse(connector.login(login, password));
    }


}

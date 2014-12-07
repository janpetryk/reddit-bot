package pl.jpetryk.redditbot.redditconnector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.jpetryk.redditbot.PropertiesReader;
import pl.jpetryk.redditbot.model.Comment;

import java.util.List;

/**
 * Created by Jan on 06/12/14.
 */
public abstract class AbstractRedditConnectorITCase<T extends RedditConnectorInterface> {

    protected abstract T createInstance();

    private PropertiesReader testProperties = new PropertiesReader("testbot.properties");

    protected T connector = createInstance();

    @Before
    public void init() {
        connector.initialize(getUserAgent());
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

    @Test
    public void testGetNewestSubredditComments() {
        int requestedNumberOfComments = 100;
        List<Comment> commentList = connector.getNewestSubredditComments("all", requestedNumberOfComments);
        Assert.assertEquals(requestedNumberOfComments, commentList.size());
    }

    protected String getUserAgent() {
        return testProperties.getProperty("reddit-useragent");
    }


}

package pl.jpetryk.redditbot.redditconnector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.jpetryk.redditbot.PropertiesReader;
import pl.jpetryk.redditbot.exceptions.ConnectionException;
import pl.jpetryk.redditbot.exceptions.InvalidCredentialsException;
import pl.jpetryk.redditbot.model.Comment;

import java.util.List;

/**
 * Created by Jan on 06/12/14.
 */
public abstract class AbstractRedditConnectorITCase<T extends RedditConnectorInterface> {

    protected abstract T createInstance();

    private PropertiesReader testProperties = new PropertiesReader("testbot.properties");

    protected T connector = createInstance();

    @Test
    public void testLoginWithValidCredentials() throws InvalidCredentialsException, ConnectionException {
        String login = testProperties.getProperty("reddit-login");
        String password = testProperties.getProperty("reddit-password");
        Assert.assertNotNull(connector.login(login, password));
    }

    @Test(expected = InvalidCredentialsException.class)
    public void testLoginWithInvalidCredentials() throws ConnectionException, InvalidCredentialsException {
        String login = testProperties.getProperty("reddit-login");
        String password = "asdasd";
        connector.login(login, password);
    }

    @Test
    public void testGetNewestSubredditComments() throws ConnectionException {
        int requestedNumberOfComments = 100;
        List<Comment> commentList = connector.getNewestSubredditComments("all", requestedNumberOfComments);
        Assert.assertEquals(requestedNumberOfComments, commentList.size());
    }

    protected String getUserAgent() {
        return testProperties.getProperty("reddit-useragent");
    }


}

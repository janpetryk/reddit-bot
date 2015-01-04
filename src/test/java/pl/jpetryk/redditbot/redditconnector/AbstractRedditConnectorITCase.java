package pl.jpetryk.redditbot.redditconnector;

import org.junit.Assert;
import org.junit.Test;
import pl.jpetryk.redditbot.PropertiesReader;
import pl.jpetryk.redditbot.exceptions.NetworkConnectionException;
import pl.jpetryk.redditbot.exceptions.RedditApiException;
import pl.jpetryk.redditbot.model.Comment;
import pl.jpetryk.redditbot.model.RedditLoggedInAccountInterface;

import java.util.List;

/**
 * Created by Jan on 06/12/14.
 */
public abstract class AbstractRedditConnectorITCase<T extends RedditConnectorInterface> {

    protected abstract T createInstance();

    private PropertiesReader testProperties = new PropertiesReader("testbot.properties");

    protected T connector = createInstance();

    @Test
    public void testLoginWithValidCredentials() throws RedditApiException, NetworkConnectionException {
        Assert.assertNotNull(login());
    }

    @Test
    public void testLoginWithInvalidCredentials() throws NetworkConnectionException {
        String login = testProperties.getProperty("reddit-login");
        String password = "asdasd";
        try {
            connector.login(login, password);
        } catch (RedditApiException e) {
            Assert.assertEquals("WRONG_PASSWORD", e.getReason());
        }
    }

    @Test
    public void testGetNewestSubredditComments() throws NetworkConnectionException {
        int requestedNumberOfComments = 100;
        List<Comment> commentList = connector.getNewestSubredditComments("all", requestedNumberOfComments);

        Assert.assertEquals(requestedNumberOfComments, commentList.size());
    }

    @Test
    public void testPostComment() throws NetworkConnectionException, RedditApiException {
        RedditLoggedInAccountInterface user = login();
        String message = "This is bot integration test message. Please ignore.";
        Comment commentToReply = prepareTestCommentToReply();
        String commentId = connector.postComment(user, commentToReply, message);
        Assert.assertNotNull(commentId);
        Assert.assertEquals(message, getCommentBody(commentId, testProperties.getProperty("comment-to-reply-link-id")));

    }

    /**
     * Implementation of getting comment body should be tied to the connector implementation and reddit api wrapper
     * hence delegation
     *
     * @param commentId
     * @param linkId
     * @return
     * @throws NetworkConnectionException
     */
    protected abstract String getCommentBody(String commentId, String linkId) throws NetworkConnectionException;

    protected String getUserAgent() {
        return testProperties.getProperty("reddit-useragent");
    }

    protected RedditLoggedInAccountInterface login() throws RedditApiException, NetworkConnectionException {
        String login = testProperties.getProperty("reddit-login");
        String password = testProperties.getProperty("reddit-password");
        return connector.login(login, password);
    }

    protected Comment prepareTestCommentToReply() {
        return new Comment.Builder().linkId(testProperties.getProperty("comment-to-reply-link-id"))
                .commentId(testProperties.getProperty("comment-to-reply-id")).build();
    }


}

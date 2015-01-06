package pl.jpetryk.redditbot.connectors;

import org.junit.Assert;
import org.junit.Test;
import pl.jpetryk.redditbot.model.PostCommentResult;
import pl.jpetryk.redditbot.model.RedditLoggedInAccountInterface;
import pl.jpetryk.redditbot.utils.PropertiesReader;
import pl.jpetryk.redditbot.exceptions.NetworkConnectionException;
import pl.jpetryk.redditbot.exceptions.RedditApiException;
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
    public void testLoginWithValidCredentials() throws RedditApiException, NetworkConnectionException {
        Assert.assertNotNull(loginStandard());
    }

    @Test
    public void testLoginWithInvalidCredentials() throws NetworkConnectionException {
        String login = testProperties.getProperty("reddit-login");
        String password = "asdasd";
        try {
            connector.loginStandard(login, password);
        } catch (RedditApiException e) {
            Assert.assertEquals("WRONG_PASSWORD", e.getReason());
        }
    }

    @Test
    public void testLoginOAuthWithValidCredentials() throws NetworkConnectionException, RedditApiException {
        Assert.assertNotNull(loginOAuth());
    }

    @Test
    public void testGetNewestSubredditComments() throws NetworkConnectionException {
        int requestedNumberOfComments = 100;
        List<Comment> commentList = connector.getNewestSubredditComments("all");
        Assert.assertEquals(requestedNumberOfComments, commentList.size());
    }

    @Test
    public void testPostComment() throws NetworkConnectionException, RedditApiException {
        RedditLoggedInAccountInterface user = loginStandard();
        String message = "This is bot integration test message. Please ignore.";
        Comment commentToReply = prepareTestCommentToReply();
        PostCommentResult result = connector.replyToComment(user, commentToReply.getCommentFullName(), message);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(message, getCommentBody(result.getResponseCommentId(),
                testProperties.getProperty("comment-to-reply-link-id")));
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

    protected RedditLoggedInAccountInterface loginStandard() throws RedditApiException, NetworkConnectionException {
        String login = testProperties.getProperty("reddit-login");
        String password = testProperties.getProperty("reddit-password");
        return connector.loginStandard(login, password);
    }

    protected RedditLoggedInAccountInterface loginOAuth() throws NetworkConnectionException, RedditApiException {
        String login = testProperties.getProperty("reddit-login");
        String password = testProperties.getProperty("reddit-password");
        String clientId = testProperties.getProperty("reddit-client-id");
        String clientSecret = testProperties.getProperty("reddit-client-secret");
        return connector.loginOAuth(login, password, clientId, clientSecret);
    }

    protected Comment prepareTestCommentToReply() {
        return new Comment.Builder().linkId(testProperties.getProperty("comment-to-reply-link-id"))
                .commentId(testProperties.getProperty("comment-to-reply-id")).build();
    }


}

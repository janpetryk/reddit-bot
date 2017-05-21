package pl.jpetryk.redditbot.connectors;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import pl.jpetryk.redditbot.model.PostCommentResult;
import pl.jpetryk.redditbot.utils.PropertiesReader;
import pl.jpetryk.redditbot.exceptions.NetworkConnectionException;
import pl.jpetryk.redditbot.exceptions.RedditApiException;
import pl.jpetryk.redditbot.model.Comment;

import java.util.List;

/**
 * Created by Jan on 06/12/14.
 */
public abstract class AbstractRedditConnectorITCase<T extends RedditConnectorInterface> {

    protected abstract T createValidInstance() throws NetworkConnectionException, RedditApiException;

    protected abstract T createInvalidInstance() throws NetworkConnectionException, RedditApiException;

    protected PropertiesReader testProperties = new PropertiesReader("testbot.properties");

    protected T connector;


    @Test
    public void testLoginWithValidCredentials() throws NetworkConnectionException {
        try {
            connector = createValidInstance();
        } catch (RedditApiException e) {
            fail();
        }
    }

    @Test(expected = RedditApiException.class)
    public void testLoginWithInvalidCredentials() throws NetworkConnectionException, RedditApiException {
        createInvalidInstance();
    }


    @Test
    public void testGetNewestSubredditComments() throws NetworkConnectionException, RedditApiException {
        connector = createValidInstance();
        List<Comment> commentList = connector.getNewestSubredditComments("all");
        assertEquals(RedditConnectorInterface.MAX_COMMENTS_PER_REQUEST, commentList.size());
        for (Comment comment : commentList) {
            assertAllFieldsAreSet(comment);
        }
    }

    private void assertAllFieldsAreSet(Comment comment) {
        assertNotNull(comment.getCreated());
        assertNotNull(comment.getAuthor());
        assertNotNull(comment.getBody());
        assertNotNull(comment.getCommentFullName());
        assertNotNull(comment.getCommentId());
        assertNotNull(comment.getLinkId());
        assertNotNull(comment.getLinkUrl());
        assertNotNull(comment.getLinkTitle());
    }

    @Test
    @Ignore
    public void testPostComment() throws NetworkConnectionException, RedditApiException {
        connector = createValidInstance();
        String message = "This is bot integration test message. Please ignore.";
        Comment commentToReply = prepareTestCommentToReply();
        PostCommentResult result = connector.replyToComment(commentToReply.getCommentFullName(), message);
        assertTrue("Could not post test comment, possible reddit api ratelimit", result.isSuccess());
        assertEquals(message, getCommentBody(result.getResponseCommentId(),
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

    protected Comment prepareTestCommentToReply() {
        return new Comment.Builder().linkId(testProperties.getProperty("comment-to-reply-link-id"))
                .commentId(testProperties.getProperty("comment-to-reply-id")).build();
    }


}

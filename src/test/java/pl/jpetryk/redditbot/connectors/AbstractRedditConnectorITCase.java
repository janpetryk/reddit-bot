package pl.jpetryk.redditbot.connectors;

import static org.junit.Assert.*;

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

    protected abstract T createValidInstance() throws Exception;

    protected abstract T createInvalidInstance() throws Exception;

    protected PropertiesReader testProperties = new PropertiesReader("testbot.properties");

    protected T connector;


    @Test
    public void testLoginWithValidCredentials() throws Exception {
        try {
            connector = createValidInstance();
        } catch (RedditApiException e) {
            fail();
        }
    }

    @Test(expected = RedditApiException.class)
    public void testLoginWithInvalidCredentials() throws Exception {
        createInvalidInstance();
    }


    @Test
    public void testGetNewestSubredditComments() throws Exception {
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
    public void testPostComment() throws Exception {
        connector = createValidInstance();
        String message = "This is bot integration test message. Please ignore.";
        Comment commentToReply = prepareTestCommentToReply();
        PostCommentResult result = connector.replyToComment(commentToReply.getCommentFullName(), message);
        assertTrue("Could not post test comment, possible reddit api ratelimit", result.isSuccess());
    }

    protected Comment prepareTestCommentToReply() {
        return new Comment.Builder().linkId(testProperties.getProperty("comment-to-reply-link-id"))
                .commentId(testProperties.getProperty("comment-to-reply-id")).build();
    }


}

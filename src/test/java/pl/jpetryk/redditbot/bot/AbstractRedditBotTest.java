package pl.jpetryk.redditbot.bot;

import org.joda.time.DateTime;

import static org.junit.Assert.*;

import org.junit.Test;
import pl.jpetryk.redditbot.connectors.RedditConnectorInterface;
import pl.jpetryk.redditbot.exceptions.NetworkConnectionException;
import pl.jpetryk.redditbot.exceptions.RedditApiException;
import pl.jpetryk.redditbot.model.Comment;
import pl.jpetryk.redditbot.model.PostCommentResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan on 11/01/15.
 */
public class AbstractRedditBotTest {

    private static final String SUBREDDIT_NAME = "asdas";
    private static final int NUMBER_OF_ALL_COMMENTS = 100;
    private static final int NUMBER_OF_COMMENTS_TO_RESPOND = NUMBER_OF_ALL_COMMENTS / 2;

    private class ConnectorMockImpl implements RedditConnectorInterface {

        public PostCommentResult resultToReturn;
        public int replyToCommentMethodCallCount = 0;

        @Override
        public List<Comment> getNewestSubredditComments(String subredditName) throws NetworkConnectionException {
            List<Comment> resultList = new ArrayList<>(NUMBER_OF_ALL_COMMENTS);
            for (int i = 0; i < NUMBER_OF_ALL_COMMENTS; i++) {
                resultList.add(prepareComment(Integer.toString(i)));
            }
            return resultList;
        }

        @Override
        public PostCommentResult replyToComment(String parentCommentFullName, String responseCommentBody)
                throws NetworkConnectionException, RedditApiException {
            replyToCommentMethodCallCount++;
            return resultToReturn;
        }

        private Comment prepareComment(String id) {
            return new Comment.Builder().commentId(id).body(id)
                    .created(new DateTime(NUMBER_OF_ALL_COMMENTS - Integer.valueOf(id), 1, 1, 1, 1, 1)).build();
        }
    }

    private class BotMockImpl extends AbstractRedditBot {

        public BotMockImpl(RedditConnectorInterface connector, String subreddits) {
            super(connector, subreddits);
        }

        @Override
        protected boolean shouldRespondToComment(Comment comment) throws Exception {
            return Integer.valueOf(comment.getCommentId()) >= (NUMBER_OF_COMMENTS_TO_RESPOND);
        }

        @Override
        protected String responseMessage(Comment comment) throws Exception {
            return comment.getCommentId();
        }


    }

    private ConnectorMockImpl connector = new ConnectorMockImpl();
    private BotMockImpl bot = new BotMockImpl(connector, SUBREDDIT_NAME);

    @Test
    public void testSortComments() throws NetworkConnectionException {
        List<Comment> commentList = connector.getNewestSubredditComments(SUBREDDIT_NAME);
        bot.sortByDateInAscendingOrder(commentList);
        for (int i = 1; i < NUMBER_OF_ALL_COMMENTS; i++) {
            assertTrue(commentList.get(i).getCreated().getMillis()
                    > commentList.get(i - 1).getCreated().getMillis());
        }
    }


    @Test
    public void testAddMatchingCommentsToWaitingQueue() throws Exception {
        List<Comment> commentList = connector.getNewestSubredditComments(SUBREDDIT_NAME);
        bot.addMatchingCommentsToWaitingQueue(commentList);
        for (Comment comment : commentList) {
            assertTrue(bot.buffer.contains(comment));
            if (bot.shouldRespondToComment(comment)) {
                assertTrue(bot.commentToRespondQueue.contains(comment));
            } else {
                assertFalse(bot.commentToRespondQueue.contains(comment));
            }
        }
    }

    @Test
    public void testRespondToMatchingCommentsWithValidResponse() throws Exception {
        connector.resultToReturn = PostCommentResult.successful("");
        List<Comment> commentList = connector.getNewestSubredditComments(SUBREDDIT_NAME);
        bot.sortByDateInAscendingOrder(commentList);
        bot.addMatchingCommentsToWaitingQueue(commentList);
        bot.respondToMatchingComments();
        assertTrue(bot.commentToRespondQueue.isEmpty());
        assertEquals(NUMBER_OF_COMMENTS_TO_RESPOND, connector.replyToCommentMethodCallCount);
    }

    @Test
    public void testRespondToMatchingCommentsWithInvalidResponse() throws Exception {
        connector.resultToReturn = PostCommentResult.unsuccessful("RATELIMIT");
        List<Comment> commentList = connector.getNewestSubredditComments(SUBREDDIT_NAME);
        bot.sortByDateInAscendingOrder(commentList);
        bot.addMatchingCommentsToWaitingQueue(commentList);
        bot.respondToMatchingComments();
        assertEquals(NUMBER_OF_COMMENTS_TO_RESPOND, bot.commentToRespondQueue.size());
    }


}

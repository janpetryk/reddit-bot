package pl.jpetryk.redditbot.bot;

import com.google.common.annotations.VisibleForTesting;
import org.apache.log4j.Logger;
import pl.jpetryk.redditbot.exceptions.RedditApiException;
import pl.jpetryk.redditbot.model.*;
import pl.jpetryk.redditbot.connectors.RedditConnectorInterface;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Jan on 05/01/15.
 */
public abstract class AbstractRedditBot implements Runnable {

    private RedditConnectorInterface connector;

    protected Logger logger = Logger.getLogger(this.getClass());

    @VisibleForTesting
    Queue<Comment> commentToRespondQueue;

    @VisibleForTesting
    Buffer<Comment> buffer;

    private Thread responseThread;

    private String subreddits;

    public AbstractRedditBot(RedditConnectorInterface connector, String subreddits) {
        this.connector = connector;
        this.subreddits = subreddits;
        commentToRespondQueue = new ConcurrentLinkedQueue<>();
        buffer = new Buffer<>(2 * RedditConnectorInterface.MAX_COMMENTS_PER_REQUEST);
        responseThread = prepareResponseThread();
    }

    protected abstract boolean shouldRespondToComment(Comment comment) throws Exception;

    protected abstract String responseMessage(Comment comment) throws Exception;

    @Override
    public void run() {
        try {
            int startingNumberOfItems = buffer.itemsAdded();
            List<Comment> commentList = connector.getNewestSubredditComments(subreddits);
            sortByDateInAscendingOrder(commentList);
            addMatchingCommentsToWaitingQueue(commentList);
            if (!responseThread.isAlive()) {
                responseThread.run();
            }
            logger.trace("Comments processed: " + buffer.itemsAdded() + ". Waiting queue size: " +
                    commentToRespondQueue.size());
            if (buffer.itemsAdded() - startingNumberOfItems == RedditConnectorInterface.MAX_COMMENTS_PER_REQUEST &&
                    startingNumberOfItems != 0) {
                logger.warn("It is possible that some comments might be not processed. Try running bot more frequently");
            }
        } catch (RedditApiException e) {
            logger.warn(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private Thread prepareResponseThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    respondToMatchingComments();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        });
    }

    @VisibleForTesting
    void respondToMatchingComments() throws Exception {
        Iterator<Comment> iterator = commentToRespondQueue.iterator();
        while (iterator.hasNext()) {
            Comment commentToRespond = iterator.next();
            String responseMessage = responseMessage(commentToRespond);
            PostCommentResult result = connector.replyToComment(commentToRespond.getCommentFullName(),
                    responseMessage);
            if (result.isSuccess()) {
                iterator.remove();
                logger.info("Successfully responded to comment " + commentToRespond.getCommentId() +
                        ". Response id: " + result.getResponseCommentId());
            } else {
                logger.trace("Could not respond to a comment. Reason: " + result.getErrorMessage());
            }
        }
    }

    @VisibleForTesting
    void addMatchingCommentsToWaitingQueue(List<Comment> commentList) throws Exception {
        for (Comment comment : commentList) {
            if (!buffer.contains(comment)) {
                buffer.add(comment);
                if (shouldRespondToComment(comment)) {
                    commentToRespondQueue.add(comment);
                    logger.info("Added comment with id " + comment.getCommentId() + " to waiting to respond queue");
                }
            }
        }
    }

    @VisibleForTesting
    void sortByDateInAscendingOrder(List<Comment> commentList) {
        commentList.sort(new Comparator<Comment>() {
            @Override
            public int compare(Comment o1, Comment o2) {
                return (int) (o1.getCreated().getMillis() - o2.getCreated().getMillis());
            }
        });
    }

}

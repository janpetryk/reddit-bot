package pl.jpetryk.redditbot.bot;

import com.google.common.annotations.VisibleForTesting;
import org.apache.log4j.Logger;
import pl.jpetryk.redditbot.exceptions.NetworkConnectionException;
import pl.jpetryk.redditbot.model.*;
import pl.jpetryk.redditbot.connectors.RedditConnectorInterface;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Jan on 05/01/15.
 */
public abstract class AbstractRedditBot implements Runnable {

    private RedditConnectorInterface connector;

    protected Logger logger = Logger.getLogger(this.getClass());

    @VisibleForTesting
    Map<Comment, String> commentToRespondMap;

    @VisibleForTesting
    Buffer<Comment> processedCommentsBuffer;

    @VisibleForTesting
    Queue<Comment> fetchedCommentQueue;


    private Thread processAndRespondToCommentsThread;


    private String subreddits;

    public AbstractRedditBot(RedditConnectorInterface connector, String subreddits) {
        this.connector = connector;
        this.subreddits = subreddits;
        commentToRespondMap = new HashMap<>();
        processedCommentsBuffer = new Buffer<>(10 * RedditConnectorInterface.MAX_COMMENTS_PER_REQUEST);
        processAndRespondToCommentsThread = prepareProcessAndRespondToCommentsThread();
        fetchedCommentQueue = new ConcurrentLinkedQueue<>();
    }

    protected abstract ProcessCommentResult processComment(Comment comment) throws Exception;

    @Override
    public void run() {
        try {
            int startingNumberOfItems = processedCommentsBuffer.itemsAdded();
            getUniqueNewestComments();
            if (!fetchedCommentQueue.isEmpty() && !processAndRespondToCommentsThread.isAlive()) {
                processAndRespondToCommentsThread = prepareProcessAndRespondToCommentsThread();
                processAndRespondToCommentsThread.start();
            }

            logger.trace("Comments processed: " + processedCommentsBuffer.itemsAdded() + ".");
            if (processedCommentsBuffer.itemsAdded() - startingNumberOfItems == RedditConnectorInterface.MAX_COMMENTS_PER_REQUEST &&
                    startingNumberOfItems != 0) {
                logger.warn("It is possible that some comments might be not processed. Try running bot more frequently");
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @VisibleForTesting
    void getUniqueNewestComments() throws NetworkConnectionException {
        List<Comment> commentList = connector.getNewestSubredditComments(subreddits);
        for (Comment comment : commentList) {
            if (!processedCommentsBuffer.contains(comment)) {
                processedCommentsBuffer.add(comment);
                fetchedCommentQueue.add(comment);
            }
        }
    }

    @VisibleForTesting
    void addMatchingCommentsToWaitingQueue() {
        while (!fetchedCommentQueue.isEmpty()) {
            try {
                Comment comment = fetchedCommentQueue.element();
                ProcessCommentResult parseResult = processComment(comment);
                if (parseResult.shouldRespond()) {
                    commentToRespondMap.put(comment, parseResult.getResponseMessage());
                    logger.info("Added comment with id " + comment.getCommentId() + " to waiting to respond queue");
                }
                fetchedCommentQueue.remove();
            } catch (Exception e) {
                logger.error(e);
            }
        }
        logger.trace("Waiting queue size: " + commentToRespondMap.size() + ".");
    }

    @VisibleForTesting
    void respondToMatchingComments() {
        Iterator<Map.Entry<Comment, String>> iterator = commentToRespondMap.entrySet().iterator();
        while (iterator.hasNext()) {
            try {
                Map.Entry<Comment, String> entry = iterator.next();
                Comment commentToRespond = entry.getKey();
                String responseMessage = entry.getValue();
                PostCommentResult result = connector.replyToComment(commentToRespond.getCommentFullName(),
                        responseMessage);
                if (result.isSuccess()) {
                    logger.info("Successfully responded to comment " + commentToRespond.getCommentId() +
                            ". Response id: " + result.getResponseCommentId());
                } else {
                    logger.warn("Could not respond to a comment. Reason: " + result.getErrorMessage());
                }
                if (result.shouldBeDeleted()) {
                    iterator.remove();
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    private Thread prepareProcessAndRespondToCommentsThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                addMatchingCommentsToWaitingQueue();
                respondToMatchingComments();
            }
        });
    }


}

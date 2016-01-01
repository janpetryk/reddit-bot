package pl.jpetryk.redditbot.bot;

import com.google.common.annotations.VisibleForTesting;
import org.apache.log4j.Logger;
import pl.jpetryk.redditbot.exceptions.NetworkConnectionException;
import pl.jpetryk.redditbot.model.*;
import pl.jpetryk.redditbot.connectors.RedditConnectorInterface;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jan on 05/01/15.
 */
public abstract class AbstractRedditBot implements Runnable {

    private RedditConnectorInterface connector;

    protected Logger logger = Logger.getLogger(this.getClass());

    @VisibleForTesting
    Buffer<Comment> processedCommentsBuffer;

    private ExecutorService processCommentsExecutor = Executors.newFixedThreadPool(1);
    private ExecutorService respondToCommentsExecutor = Executors.newFixedThreadPool(1);

    private String subreddits;

    public AbstractRedditBot(RedditConnectorInterface connector, String subreddits) {
        this.connector = connector;
        this.subreddits = subreddits;
        processedCommentsBuffer = new Buffer<>(10 * RedditConnectorInterface.MAX_COMMENTS_PER_REQUEST);
    }

    protected abstract ProcessCommentResult processComment(Comment comment) throws Exception;

    @Override
    public void run() {
        try {
            int startingNumberOfItems = processedCommentsBuffer.itemsAdded();
            final List<Comment> uniqueNewestComments = getUniqueNewestComments();
            processCommentsExecutor.execute(() -> {
                final Map<Comment, String> commentsToRespondMap = getMatchingComments(uniqueNewestComments);
                respondToCommentsExecutor.execute(() -> respondToComments(commentsToRespondMap));
            });
            logger.trace("Comments processed: " + processedCommentsBuffer.itemsAdded() + ".");
            if (processedCommentsBuffer.itemsAdded() - startingNumberOfItems == RedditConnectorInterface.MAX_COMMENTS_PER_REQUEST &&
                    startingNumberOfItems != 0) {
                logger.warn("It is possible that some comments might be not processed. Try running bot more frequently");
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private List<Comment> getUniqueNewestComments() {
        List<Comment> result = new ArrayList<>();
        try {
            List<Comment> commentList = connector.getNewestSubredditComments(subreddits);
            commentList.stream().filter(comment -> !processedCommentsBuffer.contains(comment)).forEach(comment -> {
                processedCommentsBuffer.add(comment);
                result.add(comment);
            });
        } catch (NetworkConnectionException e) {
            logger.error("Error while fetching newest comments", e);
        }
        return result;
    }


    private void respondToComments(Map<Comment, String> commentToRespondMap) {
        for (Map.Entry<Comment, String> entry : commentToRespondMap.entrySet()) {
            try {
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
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }


    private Map<Comment, String> getMatchingComments(List<Comment> commentsToProcess) {
        Map<Comment, String> result = new LinkedHashMap<>();
        try {
            for (Comment comment : commentsToProcess) {
                ProcessCommentResult parseResult = processComment(comment);
                if (parseResult.shouldRespond()) {
                    result.put(comment, parseResult.getResponseMessage());
                    logger.info("Added comment with id " + comment.getCommentId() + " to waiting to respond queue");
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return result;
    }


}

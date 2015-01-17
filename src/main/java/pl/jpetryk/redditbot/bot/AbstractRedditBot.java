package pl.jpetryk.redditbot.bot;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.log4j.Logger;
import pl.jpetryk.redditbot.exceptions.NetworkConnectionException;
import pl.jpetryk.redditbot.exceptions.RedditApiException;
import pl.jpetryk.redditbot.model.*;
import pl.jpetryk.redditbot.connectors.RedditConnectorInterface;

import java.util.*;

/**
 * Created by Jan on 05/01/15.
 */
public abstract class AbstractRedditBot implements Runnable {

    private RedditConnectorInterface connector;

    protected Logger logger = Logger.getLogger(this.getClass());

    @VisibleForTesting
    Map<Comment, String> commentToRespondMap;

    @VisibleForTesting
    Buffer<Comment> buffer;

    private String subreddits;

    public AbstractRedditBot(RedditConnectorInterface connector, String subreddits) {
        this.connector = connector;
        this.subreddits = subreddits;
        commentToRespondMap = new HashMap<>();
        buffer = new Buffer<>(2 * RedditConnectorInterface.MAX_COMMENTS_PER_REQUEST);
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
            respondToMatchingComments();
            logger.trace("Comments processed: " + buffer.itemsAdded() + ". Waiting queue size: " +
                    commentToRespondMap.size());
            if(buffer.itemsAdded() - startingNumberOfItems == connector.MAX_COMMENTS_PER_REQUEST){
                logger.warn("It is possible that some comments might be not processed. Try running bot more frequently");
            }
        } catch (RedditApiException e) {
            logger.warn(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @VisibleForTesting
    void respondToMatchingComments() throws NetworkConnectionException, RedditApiException {
        Iterator<Comment> iterator = commentToRespondMap.keySet().iterator();
        while (iterator.hasNext()) {
            Comment commentToRespond = iterator.next();
            String responseMessage = commentToRespondMap.get(commentToRespond);
            PostCommentResult result = connector.replyToComment(commentToRespond.getCommentFullName(),
                    responseMessage);
            if (result.isSuccess()) {
                iterator.remove();
                logger.info("Successfully responded to comment " + commentToRespond.getCommentId() +
                        ". Response id: " + result.getResponseCommentId());
            } else {
                logger.info("Could not respond to a comment. Reason: " + result.getErrorMessage());
                if (result.isRateLimitError()) {
                    // if this is a reddit api rate limit error then there is no point in continuing  - try next
                    // iteration. If on the other hand it is not, then try to post another comments, because this one
                    // can be corrupted in other way (for example deleted)
                    return;
                }
            }
        }
    }

    @VisibleForTesting
    void addMatchingCommentsToWaitingQueue(List<Comment> commentList) {
        for (Comment comment : commentList) {
            if (!buffer.contains(comment)) {
                buffer.add(comment);
                try {
                    if (shouldRespondToComment(comment)) {
                        String responseMessage = responseMessage(comment);
                        commentToRespondMap.put(comment, responseMessage);
                        logger.info("Added comment with id " + comment.getCommentId() + " to waiting to respond queue");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

package pl.jpetryk.redditbot;

import org.apache.log4j.Logger;
import pl.jpetryk.redditbot.exceptions.NetworkConnectionException;
import pl.jpetryk.redditbot.exceptions.RedditApiException;
import pl.jpetryk.redditbot.model.*;
import pl.jpetryk.redditbot.connectors.JrawRedditConnector;
import pl.jpetryk.redditbot.connectors.RedditConnectorInterface;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Jan on 05/01/15.
 */
public abstract class AbstractRedditBot implements Runnable {

    private Configuration configuration;

    private RedditConnectorInterface connector;

    protected Logger logger = Logger.getLogger(this.getClass());

    private RedditLoggedInAccountInterface loggedInAccount;

    private Queue<Comment> commentToRespondQueue;

    private Buffer<Comment> buffer;


    public AbstractRedditBot(Configuration configuration) {
        try {
            this.configuration = configuration;
            connector = new JrawRedditConnector(configuration.getUserAgent());
            logger.trace("Created new connector with user agent " + configuration.getUserAgent());
            commentToRespondQueue = new LinkedList<>();
            buffer = new Buffer<>();
            loggedInAccount = login();
        } catch (NetworkConnectionException e) {
            logger.error(e.getMessage());
        } catch (RedditApiException e) {
            logger.warn(e.getMessage());
        }

    }

    public abstract boolean shouldRespondToComment(Comment comment);

    public abstract String responseMessage(Comment comment);

    @Override
    public void run() {
        try {
            List<Comment> commentList = connector.getNewestSubredditComments(configuration.getAbsoluteSubredditPath());
            for (Comment comment : commentList) {
                if (!buffer.contains(comment)) {
                    buffer.add(comment);
                    if (shouldRespondToComment(comment)) {
                        commentToRespondQueue.add(comment);
                        logger.info("Added comment with id " + comment.getCommentId() + " to waiting to respond queue");
                    }
                }
            }
            Iterator<Comment> iterator = commentToRespondQueue.iterator();
            while (iterator.hasNext()) {
                Comment commentToRespond = iterator.next();
                PostCommentResult result = connector.replyToComment(loggedInAccount,
                        commentToRespond.getCommentFullName(), responseMessage(commentToRespond));
                if (result.isSuccess()) {
                    iterator.remove();
                    logger.info("Successfully responded to comment " + commentToRespond.getCommentId() +
                            ". Response id: " + result.getResponseCommentId());
                } else {
                    logger.info("Could not respond to a comment. Reason: " + result.getErrorMessage());
                }
            }
            buffer.invalidateOldItems();
            logger.trace("Comments processed: " + buffer.itemsAdded() + ". Waiting queue size: " + commentToRespondQueue.size());
        } catch (NetworkConnectionException e) {
            logger.error(e.getMessage());
        } catch (RedditApiException e) {
            logger.warn(e.getMessage());
        }
    }

    private RedditLoggedInAccountInterface login() throws NetworkConnectionException, RedditApiException {
        RedditLoggedInAccountInterface account;
        if (configuration.getAuthorizationType() == AuthorizationType.OAUTH) {
            account = connector.loginOAuth(configuration.getLogin(), configuration.getPassword(),
                    configuration.getClientId(), configuration.getClientSecret());
            logger.trace("Successfully logged in as " + configuration.getLogin() + " with OAuth authorization");
        } else {
            account = connector.loginStandard(configuration.getLogin(), configuration.getPassword());
            logger.trace("Successfully logged in as " + configuration.getLogin() + " with standard authorization");
        }
        return account;
    }
}

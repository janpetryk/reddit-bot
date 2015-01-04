package pl.jpetryk.redditbot.redditconnector;

import pl.jpetryk.redditbot.exceptions.NetworkConnectionException;
import pl.jpetryk.redditbot.exceptions.RedditApiException;
import pl.jpetryk.redditbot.model.Comment;
import pl.jpetryk.redditbot.model.RedditLoggedInAccountInterface;

import java.util.List;

/**
 * Interface that describes reddit connector, separates app from reddit library implementation
 */
public interface RedditConnectorInterface {

    public RedditLoggedInAccountInterface login(String login, String password) throws RedditApiException, NetworkConnectionException;

    public List<Comment> getNewestSubredditComments(String subredditName, int numberOfComments) throws NetworkConnectionException;

    public String postComment(RedditLoggedInAccountInterface user, Comment parentComment, String commentBody) throws NetworkConnectionException, RedditApiException;
}

package pl.jpetryk.redditbot.connectors;

import pl.jpetryk.redditbot.exceptions.NetworkConnectionException;
import pl.jpetryk.redditbot.exceptions.RedditApiException;
import pl.jpetryk.redditbot.model.Comment;
import pl.jpetryk.redditbot.model.PostCommentResult;
import pl.jpetryk.redditbot.model.RedditLoggedInAccountInterface;

import java.util.List;

/**
 * Interface that describes reddit connector, separates app from reddit library implementation
 */
public interface RedditConnectorInterface {

    public RedditLoggedInAccountInterface loginStandard(String login, String password)
            throws RedditApiException, NetworkConnectionException;

    public RedditLoggedInAccountInterface loginOAuth(String username, String password, String clientId,
                                             String clientSecret)
            throws RedditApiException, NetworkConnectionException;

    public List<Comment> getNewestSubredditComments(String subredditName)
            throws NetworkConnectionException;

    public PostCommentResult replyToComment(RedditLoggedInAccountInterface user, String parentCommentFullName, String commentBody)
            throws NetworkConnectionException, RedditApiException;

}

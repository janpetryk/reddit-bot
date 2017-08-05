package pl.jpetryk.redditbot.connectors;

import pl.jpetryk.redditbot.exceptions.NetworkConnectionException;
import pl.jpetryk.redditbot.exceptions.RedditApiException;
import pl.jpetryk.redditbot.model.Comment;
import pl.jpetryk.redditbot.model.PostCommentResult;

import java.util.List;

/**
 * Interface that describes reddit connector, separates app from reddit library implementation
 */
public interface RedditConnectorInterface {

    int MAX_COMMENTS_PER_REQUEST = 100;

    List<Comment> getNewestSubredditComments(String subredditName)
            throws NetworkConnectionException;

    PostCommentResult replyToComment(String parentCommentFullName, String responseCommentBody)
            throws NetworkConnectionException, RedditApiException;

}

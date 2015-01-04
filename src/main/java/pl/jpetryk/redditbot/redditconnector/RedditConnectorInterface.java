package pl.jpetryk.redditbot.redditconnector;

import pl.jpetryk.redditbot.exceptions.ConnectionException;
import pl.jpetryk.redditbot.exceptions.InvalidCredentialsException;
import pl.jpetryk.redditbot.model.Comment;
import pl.jpetryk.redditbot.model.LoggedInUserInterface;

import java.util.List;

/**
 * Interface that describes reddit connector, separates app from reddit library implementation
 */
public interface RedditConnectorInterface {

    public LoggedInUserInterface login(String login, String password) throws InvalidCredentialsException, ConnectionException;

    public List<Comment> getNewestSubredditComments(String subredditName, int numberOfComments) throws ConnectionException;


}

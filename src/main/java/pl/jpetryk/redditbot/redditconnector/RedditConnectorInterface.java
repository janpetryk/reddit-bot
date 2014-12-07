package pl.jpetryk.redditbot.redditconnector;

import pl.jpetryk.redditbot.model.Comment;

import java.util.List;

/**
 * Interface that describes reddit connector, separates app from reddit library implementation
 */
public interface RedditConnectorInterface {

    public void initialize(String userAgent);

    public boolean login(String login, String password);

    public List<Comment> getNewestSubredditComments(String subredditName, int numberOfComments);


}

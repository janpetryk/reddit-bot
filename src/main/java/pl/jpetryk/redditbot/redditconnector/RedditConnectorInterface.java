package pl.jpetryk.redditbot.redditconnector;

/**
 * Interface that describes reddit connector, separates app from reddit library implementation
 */
public interface RedditConnectorInterface {

    public void initialize(String userAgent);

    public boolean login(String login, String password);

}

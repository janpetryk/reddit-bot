package pl.jpetryk.redditbot.redditconnector;

import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.Credentials;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.LoggedInAccount;
import org.apache.log4j.Logger;

/**
 * Created by Jan on 06/12/14.
 */
public class JrawRedditConnector implements RedditConnectorInterface {


    private RedditClient redditClient;
    private static final Logger logger = Logger.getLogger(JrawRedditConnector.class);


    @Override
    public void initialize(String userAgent) {
        redditClient = new RedditClient(userAgent);
    }

    @Override
    public boolean login(String login, String password) {
        try {
            LoggedInAccount account = redditClient.login(Credentials.standard(login, password));
            logger.info("Logged in as " + login);
            return true;
        } catch (NetworkException | ApiException e) {
            logger.warn("Could not log in as " + login, e);
            return false;
        }
    }
}

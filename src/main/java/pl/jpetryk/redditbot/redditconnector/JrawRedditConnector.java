package pl.jpetryk.redditbot.redditconnector;

import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.Credentials;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.LoggedInAccount;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import pl.jpetryk.redditbot.model.Comment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jan on 06/12/14.
 */
public class JrawRedditConnector implements RedditConnectorInterface {


    private RedditClient redditClient;
    private static final Logger logger = Logger.getLogger(JrawRedditConnector.class);
    private LoggedInAccount account;

    @Override
    public void initialize(String userAgent) {
        redditClient = new RedditClient(userAgent);
    }

    @Override
    public boolean login(String login, String password) {
        try {
            account = redditClient.login(Credentials.standard(login, password));
            boolean loggedIn = redditClient.isLoggedIn();
            if (loggedIn) {
                logger.info("Logged in as " + login);
                return true;
            } else {
                logger.warn("Could not log in as " + login);
                return false;
            }
        } catch (NetworkException | ApiException e) {
            logger.warn("Could not log in as " + login, e);
            return false;
        }
    }

    @Override
    public List<Comment> getNewestSubredditComments(String subredditName, int numberOfComments) {
        try {
            String subredditPath = JrawUtils.getSubredditPath(subredditName, "/comments.json") + "?limit=" + Integer.toString(numberOfComments);
            JsonNode response = redditClient.execute(redditClient.request().path(subredditPath).build()).getJson();
            List<Comment> result = new ArrayList<>();
            Iterator<JsonNode> iterator = response.get("data").get("children").getElements();
            while (iterator.hasNext()) {
                JsonNode jsonNode = iterator.next().get("data");
                Comment comment = Comment.builder().id(jsonNode.get("id").asText()).body(jsonNode.get("body")
                        .asText()).build();
                result.add(comment);
            }
            return result;
        } catch (NetworkException e) {
            logger.error("Network error", e);
            return new ArrayList<>();
        }
    }
}

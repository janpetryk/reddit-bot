package pl.jpetryk.redditbot.redditconnector;

import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.Credentials;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.LoggedInAccount;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import pl.jpetryk.redditbot.exceptions.ConnectionException;
import pl.jpetryk.redditbot.exceptions.InvalidCredentialsException;
import pl.jpetryk.redditbot.model.Comment;
import pl.jpetryk.redditbot.model.JrawLoggedInUserAdapter;
import pl.jpetryk.redditbot.model.LoggedInUserInterface;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jan on 06/12/14.
 */
public class JrawRedditConnector implements RedditConnectorInterface {


    private RedditClient redditClient;
    private static final Logger logger = Logger.getLogger(JrawRedditConnector.class);

    public JrawRedditConnector(String userAgent) {
        redditClient = new RedditClient(userAgent);
    }

    @Override
    public LoggedInUserInterface login(String login, String password) throws InvalidCredentialsException, ConnectionException {
        try {
            return new JrawLoggedInUserAdapter(redditClient.login(Credentials.standard(login, password)));
        } catch (NetworkException e) {
            throw new ConnectionException(e);
        } catch (ApiException e) {
            throw new InvalidCredentialsException("Wrong credentials for user: " + login, e);
        }

    }

    @Override
    public List<Comment> getNewestSubredditComments(String subredditName, int numberOfComments) throws ConnectionException {
        try {
            List<Comment> result = new ArrayList<>();
            String subredditPath = JrawUtils.getSubredditPath(subredditName, "/comments.json") + "?limit=" + Integer.toString(numberOfComments);
            JsonNode response = redditClient.execute(redditClient.request().path(subredditPath).build()).getJson();
            Iterator<JsonNode> iterator = response.get("data").get("children").getElements();
            while (iterator.hasNext()) {
                JsonNode jsonNode = iterator.next().get("data");
                Comment comment = new Comment.Builder(jsonNode.get("id").asText()).body(jsonNode.get("body")
                        .asText()).build();
                result.add(comment);
            }
            logger.info(result.size() + " comments read from " + subredditName + " subreddit");
            return result;

        } catch (NetworkException e) {
            throw new ConnectionException(e);
        }
    }
}

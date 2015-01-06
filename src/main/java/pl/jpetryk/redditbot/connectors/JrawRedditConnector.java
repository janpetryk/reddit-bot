package pl.jpetryk.redditbot.connectors;

import net.dean.jraw.ApiException;
import net.dean.jraw.Endpoints;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.Credentials;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RedditResponse;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.joda.time.DateTime;
import pl.jpetryk.redditbot.exceptions.NetworkConnectionException;
import pl.jpetryk.redditbot.exceptions.RedditApiException;
import pl.jpetryk.redditbot.model.Comment;
import pl.jpetryk.redditbot.model.JrawLoggedInUserAdapter;
import pl.jpetryk.redditbot.model.PostCommentResult;
import pl.jpetryk.redditbot.model.RedditLoggedInAccountInterface;

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
    public RedditLoggedInAccountInterface loginStandard(String login, String password)
            throws RedditApiException, NetworkConnectionException {
        try {
            return new JrawLoggedInUserAdapter(redditClient.login(Credentials.standard(login, password)));
        } catch (NetworkException e) {
            throw new NetworkConnectionException(e);
        } catch (ApiException e) {
            throw new RedditApiException(e.getReason(), e.getExplanation());
        }
    }

    @Override
    public RedditLoggedInAccountInterface loginOAuth(String username, String password, String clientId, String clientSecret) throws RedditApiException, NetworkConnectionException {
        try {
            return new JrawLoggedInUserAdapter(redditClient.login(Credentials.webapp(username, password, clientId, clientSecret)));
        } catch (NetworkException e) {
            throw new NetworkConnectionException(e);
        } catch (ApiException e) {
            throw new RedditApiException(e.getReason(), e.getExplanation());
        }
    }

    @Override
    public List<Comment> getNewestSubredditComments(String subredditName)
            throws NetworkConnectionException {
        try {
            List<Comment> result = new ArrayList<>();
            String subredditPath = JrawUtils.getSubredditPath(subredditName, "/comments.json") + "?limit=100";
            JsonNode response = redditClient.execute(redditClient.request().path(subredditPath).build()).getJson();
            Iterator<JsonNode> iterator = response.get("data").get("children").getElements();
            while (iterator.hasNext()) {
                JsonNode jsonNode = iterator.next().get("data");
                Comment comment = new Comment.Builder()
                        .commentId(jsonNode.get("id").asText())
                        .body(jsonNode.get("body").asText())
                        .linkId(jsonNode.get("link_id").asText().substring(3))//unfortunately reddit api is inconsistent
                                // when it comes to comments, it displays comment id in format of base 36 id (without prefix)
                                // and link id with prefix. Substring here gets rid of prefix
                        .linkUrl(jsonNode.get("link_url").asText())
                        .created(new DateTime(jsonNode.get("created").asLong() * 1000))
                        .author(jsonNode.get("author").asText())
                        .build();
                result.add(comment);
            }
            logger.trace(result.size() + " comments read from " + subredditName + " subreddit");
            return result;

        } catch (NetworkException e) {
            throw new NetworkConnectionException(e);
        }
    }

    @Override
    public PostCommentResult replyToComment(RedditLoggedInAccountInterface user, String parentCommentFullName, String commentBody)
            throws NetworkConnectionException, RedditApiException {
        try {
            RedditResponse response = redditClient.execute(redditClient.request()
                    .endpoint(Endpoints.COMMENT)
                    .post(JrawUtils.args(
                            "api_type", "json",
                            "text", commentBody,
                            "thing_id", parentCommentFullName))
                    .build());
            return processResponse(response);
        } catch (NetworkException e) {
            throw new NetworkConnectionException(e);
        }
    }

    private PostCommentResult processResponse(RedditResponse response) {
        if (response.hasErrors()) {
            return PostCommentResult.unsuccessful(response.getErrors()[0].getMessage());
        } else {
            return PostCommentResult.successful(response.getJson().get("json").get("data").get("things").get(0)
                    .get("data").get("id").asText().substring(3));
        }
    }


    /*package*/ net.dean.jraw.models.Comment getComment(String commentId, String linkId)
            throws NetworkConnectionException {
        try {
            return redditClient.getSubmission(new RedditClient.SubmissionRequest(linkId).focus(commentId))
                    .getComments().get(0);
        } catch (NetworkException e) {
            throw new NetworkConnectionException(e);
        }
    }
}

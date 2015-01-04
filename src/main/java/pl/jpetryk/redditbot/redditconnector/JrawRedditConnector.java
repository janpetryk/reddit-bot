package pl.jpetryk.redditbot.redditconnector;

import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.Credentials;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.managers.AccountManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import pl.jpetryk.redditbot.exceptions.NetworkConnectionException;
import pl.jpetryk.redditbot.exceptions.RedditApiException;
import pl.jpetryk.redditbot.model.Comment;
import pl.jpetryk.redditbot.model.JrawLoggedInUserAdapter;
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
    public RedditLoggedInAccountInterface login(String login, String password) throws RedditApiException, NetworkConnectionException {
        try {
            return new JrawLoggedInUserAdapter(redditClient.login(Credentials.standard(login, password)));
        } catch (NetworkException e) {
            throw new NetworkConnectionException(e);
        } catch (ApiException e) {
            throw new RedditApiException(e.getReason(), e.getExplanation());
        }

    }

    @Override
    public List<Comment> getNewestSubredditComments(String subredditName, int numberOfComments) throws NetworkConnectionException {
        try {
            List<Comment> result = new ArrayList<>();
            String subredditPath = JrawUtils.getSubredditPath(subredditName, "/comments.json") + "?limit=" + Integer.toString(numberOfComments);
            JsonNode response = redditClient.execute(redditClient.request().path(subredditPath).build()).getJson();
            Iterator<JsonNode> iterator = response.get("data").get("children").getElements();
            while (iterator.hasNext()) {
                JsonNode jsonNode = iterator.next().get("data");
                Comment comment = new Comment.Builder()
                        .commentId(jsonNode.get("id").asText())
                        .body(jsonNode.get("body").asText())
                        .linkId(jsonNode.get("link_id").asText().substring(3))//unfortunately reddit api is inconsistent when it comes to comments, it displays comment id in format of base 36 id (without prefix) and link id with prefix. Substring here gets rid of prefix
                        .build();
                result.add(comment);
            }
            logger.info(result.size() + " comments read from " + subredditName + " subreddit");
            return result;

        } catch (NetworkException e) {
            throw new NetworkConnectionException(e);
        }
    }

    @Override
    public String postComment(RedditLoggedInAccountInterface user, Comment parentComment, String commentBody)
            throws NetworkConnectionException, RedditApiException {
        try {
            AccountManager accountManager = new AccountManager(redditClient);
            net.dean.jraw.models.Comment commentToReply = getComment(parentComment.getCommentId(),
                    parentComment.getLinkId());
            String repliedCommentId = accountManager.reply(commentToReply, commentBody);
            return repliedCommentId.substring(3);
        } catch (NetworkException e) {
            throw new NetworkConnectionException(e);
        } catch (ApiException e) {
            throw new RedditApiException(e.getReason(),e.getExplanation());
        }
    }

    /*package*/ net.dean.jraw.models.Comment getComment(String commentId, String linkId) throws NetworkConnectionException {
        try {
            return redditClient.getSubmission(new RedditClient.SubmissionRequest(linkId).focus(commentId))
                    .getComments().get(0);
        } catch (NetworkException e) {
            throw new NetworkConnectionException(e);
        }
    }
}

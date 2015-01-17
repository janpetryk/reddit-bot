package pl.jpetryk.redditbot.connectors;

import com.google.common.annotations.VisibleForTesting;
import net.dean.jraw.ApiException;
import net.dean.jraw.Endpoints;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.Credentials;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RedditResponse;
import org.codehaus.jackson.JsonNode;
import org.joda.time.DateTime;
import pl.jpetryk.redditbot.exceptions.NetworkConnectionException;
import pl.jpetryk.redditbot.exceptions.RedditApiException;
import pl.jpetryk.redditbot.model.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jan on 06/12/14.
 */
public class JrawRedditConnector implements RedditConnectorInterface {


    private RedditClient redditClient;

    private JrawRedditConnector(Builder builder) throws NetworkConnectionException, RedditApiException {
        redditClient = new RedditClient(builder.userAgent);
        loginOAuth(builder.login, builder.password, builder.clientId, builder.clientSecret);
    }

    @VisibleForTesting
    void loginOAuth(String username, String password, String clientId, String clientSecret)
            throws RedditApiException, NetworkConnectionException {
        try {
            redditClient.login(Credentials.webapp(username, password, clientId, clientSecret));
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
            String subredditPath = JrawUtils.getSubredditPath(subredditName, "/comments.json") + "?limit="
                    + Integer.toString(MAX_COMMENTS_PER_REQUEST);
            JsonNode response = redditClient.execute(redditClient.request().path(subredditPath).build()).getJson();
            Iterator<JsonNode> iterator = response.get("data").get("children").getElements();
            while (iterator.hasNext()) {
                JsonNode jsonNode = iterator.next().get("data");
                Comment comment = new Comment.Builder()
                        .commentId(jsonNode.get("id").asText())
                        .body(jsonNode.get("body").asText())
                        .linkId(jsonNode.get("link_id").asText().substring(3))//unfortunately reddit api is inconsistent
                                // when it comes to comments, it displays comment id in format of base 36 id
                                // (without prefix)
                                // and link id with prefix. Substring here gets rid of prefix
                        .linkUrl(jsonNode.get("link_url").asText())
                        .created(new DateTime(jsonNode.get("created").asLong() * 1000))
                        .author(jsonNode.get("author").asText())
                        .subreddit(jsonNode.get("subreddit").asText())
                        .build();
                result.add(comment);
            }
            return result;

        } catch (NetworkException e) {
            throw new NetworkConnectionException(e);
        }
    }

    @Override
    public PostCommentResult replyToComment(String parentCommentFullName, String responseCommentBody)
            throws NetworkConnectionException, RedditApiException {
        try {
            RedditResponse response = redditClient.execute(redditClient.request()
                    .endpoint(Endpoints.COMMENT)
                    .post(JrawUtils.args(
                            "api_type", "json",
                            "text", responseCommentBody,
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


    @VisibleForTesting
    net.dean.jraw.models.Comment getComment(String commentId, String linkId)
            throws NetworkConnectionException {
        try {
            return redditClient.getSubmission(new RedditClient.SubmissionRequest(linkId).focus(commentId))
                    .getComments().get(0);
        } catch (NetworkException e) {
            throw new NetworkConnectionException(e);
        }
    }

    public static class Builder {

        private static final String FIELD_IS_EMPTY_MESSAGE = "Mandatory field %s is not set.";

        private String userAgent;
        private String login;
        private String clientId;
        private String clientSecret;
        private String password;

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder login(String login) {
            this.login = login;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }


        public JrawRedditConnector build() throws NetworkConnectionException, RedditApiException {
            return new JrawRedditConnector(this);
        }
    }
}

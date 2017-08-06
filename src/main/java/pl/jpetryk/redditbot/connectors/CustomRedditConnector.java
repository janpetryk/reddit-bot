package pl.jpetryk.redditbot.connectors;

import com.google.common.collect.ImmutableMap;
import okhttp3.*;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import pl.jpetryk.redditbot.exceptions.NetworkConnectionException;
import pl.jpetryk.redditbot.exceptions.RedditApiException;
import pl.jpetryk.redditbot.model.Authorization;
import pl.jpetryk.redditbot.model.Comment;
import pl.jpetryk.redditbot.model.PostCommentResult;
import twitter4j.HttpResponseCode;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomRedditConnector implements RedditConnectorInterface {

    protected Logger logger = Logger.getLogger(this.getClass());


    public static final String AUTH_HOST = "www.reddit.com";
    public static final String HOST = "oauth.reddit.com";

    private String userAgent;
    private String login;
    private String password;
    private String clientId;
    private String clientSecret;
    private String accessToken;
    private OkHttpClient okHttpClient;
    private ObjectMapper objectMapper;

    @Inject
    public CustomRedditConnector(@Named("reddit-useragent") String userAgent,
                                 @Named("reddit-login") String login,
                                 @Named("reddit-password") String password,
                                 @Named("reddit-client-id") String clientId,
                                 @Named("reddit-client-secret") String clientSecret,
                                 OkHttpClient okHttpClient,
                                 ObjectMapper objectMapper)
            throws Exception {
        this.userAgent = userAgent;
        this.login = login;
        this.password = password;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
        authorize();

    }

    private void authorize() throws IOException {
        RequestBody requestBody = requestBody(ImmutableMap.of(
                "grant_type", "password",
                "username", login,
                "password", password));
        Request request = accessTokenRequest()
                .post(requestBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        String responseBody = response.body().string();
        if (responseBody.contains("error")) {
            throw new RedditApiException("error", responseBody);
        } else {
            Authorization authorization = objectMapper.readValue(responseBody, Authorization.class);
            accessToken = authorization.getAccessToken();
        }
    }


    @Override
    public List<Comment> getNewestSubredditComments(String subredditName) throws NetworkConnectionException {
        try {
            List<Comment> result = new ArrayList<>();
            String subredditPath = "/r/" + subredditName + "/comments.json?limit=" + Integer.toString(MAX_COMMENTS_PER_REQUEST);
            Request request = request().url(httpsUrl(HOST, subredditPath)).build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                JsonNode jsonNodeResponse = objectMapper.readTree(response.body().string());
                Iterator<JsonNode> iterator = jsonNodeResponse.get("data").get("children").getElements();
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
                            .linktTitle(jsonNode.get("link_title").asText())
                            .build();
                    result.add(comment);
                }
            } else {
                if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    authorize();
                }
            }
            return result;
        } catch (IOException e) {
            throw new NetworkConnectionException(e);
        }
    }

    @Override
    public PostCommentResult replyToComment(String parentCommentFullName, String responseCommentBody) throws NetworkConnectionException, RedditApiException {
        try {
            RequestBody requestBody = requestBody(
                    ImmutableMap.of("api_type", "json",
                            "text", URLEncoder.encode(responseCommentBody, "UTF-8"),
                            "thing_id", parentCommentFullName));
            Request request = request()
                    .post(requestBody)
                    .url(httpsUrl(HOST, "/api/comment"))
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            return processResponse(response);
        } catch (IOException e) {
            logger.error(e);
            return PostCommentResult.unsuccessful(e.getMessage());
        }
    }

    private PostCommentResult processResponse(Response response) throws IOException {
        if (response.code() == HttpURLConnection.HTTP_FORBIDDEN) {
            return PostCommentResult.bannedFromThisSub();
        }
        String responseBody = response.body().string();

        if (responseBody.contains("DELETED_COMMENT")) {
            return PostCommentResult.commentDeleted();
        }
        JsonNode jsonNodeResponse = objectMapper.readTree(responseBody);
        JsonNode errors = jsonNodeResponse.get("json").get("errrors");
        if (errors == null) {
            return PostCommentResult.successful(jsonNodeResponse.get("json").get("data").get("things").get(0)
                    .get("data").get("id").asText().substring(3));
        } else {
            return PostCommentResult.unsuccessful(errors.asText());
        }
    }


    private RequestBody requestBody(Map<String, String> values) {
        String content = parseAsQueryString(values);
        return RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), content);
    }

    private String parseAsQueryString(Map<String, String> map) {
        return String.join("&", map.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.toList()));
    }

    private Request.Builder accessTokenRequest() throws IOException {
        String basicCreds = okhttp3.Credentials.basic(clientId, clientSecret);

        URL url = getAuthorizationUrl();

        return request().url(url).header("Authorization", basicCreds);
    }

    private Request.Builder request() {
        Request.Builder builder = new Request.Builder()
                .header("User-Agent", userAgent);

        if (accessToken != null) {
            builder.header("Authorization", "bearer " + accessToken);
        }
        return builder;
    }

    private URL getAuthorizationUrl() {
        return httpsUrl(AUTH_HOST, "/api/v1/access_token");
    }

    private URL httpsUrl(String host, String path) {
        try {
            return new URL("https", host, path);
        } catch (MalformedURLException e) {
            logger.error(e);
            throw new RuntimeException("Wrong url", e);
        }
    }

}

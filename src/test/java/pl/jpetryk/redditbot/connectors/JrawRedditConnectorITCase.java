package pl.jpetryk.redditbot.connectors;

import pl.jpetryk.redditbot.exceptions.NetworkConnectionException;
import pl.jpetryk.redditbot.exceptions.RedditApiException;

/**
 * Created by Jan on 06/12/14.
 */
public class JrawRedditConnectorITCase extends AbstractRedditConnectorITCase<JrawRedditConnector> {


    @Override
    protected JrawRedditConnector createValidInstance() throws NetworkConnectionException, RedditApiException {
        return new JrawRedditConnector.Builder()
                .login(testProperties.getProperty("reddit-login"))
                .password(testProperties.getProperty("reddit-password"))
                .clientId(testProperties.getProperty("reddit-client-id"))
                .clientSecret(testProperties.getProperty("reddit-client-secret"))
                .userAgent(testProperties.getProperty("reddit-useragent"))
                .build();
    }

    @Override
    protected JrawRedditConnector createInvalidInstance() throws NetworkConnectionException, RedditApiException {
        return new JrawRedditConnector.Builder()
                .login(testProperties.getProperty("reddit-login"))
                .password("invalid-password")
                .clientId(testProperties.getProperty("reddit-client-id"))
                .clientSecret(testProperties.getProperty("reddit-client-secret"))
                .userAgent(testProperties.getProperty("reddit-useragent"))
                .build();
    }

    @Override
    protected String getCommentBody(String commentId, String linkId) throws NetworkConnectionException {
        return connector.getComment(commentId, linkId).getBody().toString();
    }

}

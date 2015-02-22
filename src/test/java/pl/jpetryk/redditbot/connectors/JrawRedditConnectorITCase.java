package pl.jpetryk.redditbot.connectors;

import pl.jpetryk.redditbot.exceptions.NetworkConnectionException;
import pl.jpetryk.redditbot.exceptions.RedditApiException;

/**
 * Created by Jan on 06/12/14.
 */
public class JrawRedditConnectorITCase extends AbstractRedditConnectorITCase<JrawRedditConnector> {


    @Override
    protected JrawRedditConnector createValidInstance() throws NetworkConnectionException, RedditApiException {
        return new JrawRedditConnector(testProperties.getProperty("reddit-useragent"),
                testProperties.getProperty("reddit-login"),
                testProperties.getProperty("reddit-password"),
                testProperties.getProperty("reddit-client-id"),
                testProperties.getProperty("reddit-client-secret"));
    }

    @Override
    protected JrawRedditConnector createInvalidInstance() throws NetworkConnectionException, RedditApiException {
        return new JrawRedditConnector(testProperties.getProperty("reddit-useragent"),
                testProperties.getProperty("reddit-login"),
                "INVALID_PASSWORD",
                testProperties.getProperty("reddit-client-id"),
                testProperties.getProperty("reddit-client-secret"));
    }

    @Override
    protected String getCommentBody(String commentId, String linkId) throws NetworkConnectionException {
        return connector.getComment(commentId, linkId).getBody().toString();
    }

}

package pl.jpetryk.redditbot.connectors;

import pl.jpetryk.redditbot.exceptions.NetworkConnectionException;

/**
 * Created by Jan on 06/12/14.
 */
public class JrawRedditConnectorITCase extends AbstractRedditConnectorITCase<JrawRedditConnector> {


    @Override
    protected JrawRedditConnector createInstance() {
        return new JrawRedditConnector(getUserAgent());
    }

    @Override
    protected String getCommentBody(String commentId, String linkId) throws NetworkConnectionException {
            return connector.getComment(commentId, linkId).getBody().toString();
    }

}

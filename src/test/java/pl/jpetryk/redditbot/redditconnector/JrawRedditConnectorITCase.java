package pl.jpetryk.redditbot.redditconnector;

/**
 * Created by Jan on 06/12/14.
 */
public class JrawRedditConnectorITCase extends AbstractRedditConnectorITCase<JrawRedditConnector> {


    @Override
    protected JrawRedditConnector createInstance() {
        return new JrawRedditConnector();
    }
}

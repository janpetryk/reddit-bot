package pl.jpetryk.redditbot.connectors;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import pl.jpetryk.redditbot.exceptions.TwitterApiException;
import pl.jpetryk.redditbot.model.Tweet;

/**
 * Created by Jan on 09/01/15.
 */
public abstract class AbstractTwitterConnectorITCase<T extends TwitterConnectorInterface> {

    private T connector;

    protected abstract T createInstance();


    @Before
    public void init() {
        connector = createInstance();
    }

    @Test
    public void testShowStatus() throws TwitterApiException {
        Tweet tweet = connector.showStatus(553930800929210368L);
        assertEquals("test bot action", tweet.getBody());
        assertNotNull(tweet.getId());
        assertNotNull(tweet.getDatePosted());
        assertNotNull(tweet.getPosterScreenName());

    }

}

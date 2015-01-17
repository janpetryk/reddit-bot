package pl.jpetryk.redditbot.connectors;

import pl.jpetryk.redditbot.exceptions.TwitterApiException;
import pl.jpetryk.redditbot.model.Tweet;

/**
 * Created by Jan on 09/01/15.
 */
public interface TwitterConnectorInterface {
    Tweet showStatus(Long id) throws TwitterApiException;
}

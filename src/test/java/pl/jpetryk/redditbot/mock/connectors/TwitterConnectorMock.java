package pl.jpetryk.redditbot.mock.connectors;

import org.joda.time.DateTime;
import pl.jpetryk.redditbot.connectors.TwitterConnectorInterface;
import pl.jpetryk.redditbot.exceptions.TwitterApiException;
import pl.jpetryk.redditbot.model.Tweet;

/**
 * Created by Jan on 2015-12-30.
 */
public class TwitterConnectorMock implements TwitterConnectorInterface {


    @Override
    public Tweet showStatus(Long id) throws TwitterApiException {
        try {
            Thread.sleep(50L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Tweet.Builder()
                .body("tweetBody" + id)
                .datePosted(new DateTime())
                .id(id)
                .poster("tweetPoster" + id)
                .build();
    }
}

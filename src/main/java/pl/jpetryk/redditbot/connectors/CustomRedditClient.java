package pl.jpetryk.redditbot.connectors;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.HttpLogger;

import java.util.concurrent.TimeUnit;

/**
 * Created by Jan on 18/01/15.
 */
public class CustomRedditClient extends RedditClient {

    public CustomRedditClient(String userAgent, int requestsPerMinute) {
        super(userAgent, requestsPerMinute);
        http.setConnectTimeout(15, TimeUnit.SECONDS);
        http.setReadTimeout(15, TimeUnit.SECONDS);
        for(HttpLogger.Component component : HttpLogger.Component.values()){
            logger.disable(component);
        }
    }
}

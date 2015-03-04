package pl.jpetryk.redditbot.connectors;


import org.joda.time.DateTime;
import pl.jpetryk.redditbot.exceptions.TwitterApiException;
import pl.jpetryk.redditbot.model.Tweet;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by Jan on 09/01/15.
 */
public class Twitter4JConnector implements TwitterConnectorInterface {

    private Twitter twitter;

    @Inject
    public Twitter4JConnector(@Named("api-key")String apiKey,
                              @Named("api-secret")String apiSecret,
                              @Named("access-token")String accessToken,
                              @Named("access-token-secret")String accessTokenSecret) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(apiKey);
        configurationBuilder.setOAuthConsumerSecret(apiSecret);
        configurationBuilder.setOAuthAccessToken(accessToken);
        configurationBuilder.setOAuthAccessTokenSecret(accessTokenSecret);
        TwitterFactory twitterFactory = new TwitterFactory(configurationBuilder.build());
        twitter = twitterFactory.getInstance();
    }

    @Override
    public Tweet showStatus(Long id) throws TwitterApiException {
        try {
            Status status = twitter.showStatus(id);
            Tweet.Builder tweetBuilder = new Tweet.Builder()
                    .body(status.getText())
                    .datePosted(new DateTime(status.getCreatedAt()))
                    .id(id)
                    .poster(status.getUser().getScreenName());
            prepareEntities(status, tweetBuilder);
            return tweetBuilder.build();
        } catch (TwitterException e) {
            throw new TwitterApiException(e, e.exceededRateLimitation(),
                    (e.getRateLimitStatus() != null && e.getRateLimitStatus().getSecondsUntilReset() > 0) ?
                            e.getRateLimitStatus().getSecondsUntilReset() * 1000 : 0, e.getErrorCode());
        }
    }

    private void prepareEntities(Status status, Tweet.Builder tweetBuilder) {
        for (URLEntity urlEntity : status.getURLEntities()) {
            tweetBuilder.addUrlEntity(urlEntity.getURL(), urlEntity.getExpandedURL());
        }
        for (MediaEntity mediaEntity : status.getMediaEntities()) {
            tweetBuilder.addImageEntity(mediaEntity.getURL(), mediaEntity.getMediaURL());
        }
    }



}

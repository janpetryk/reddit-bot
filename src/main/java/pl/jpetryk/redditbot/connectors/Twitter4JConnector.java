package pl.jpetryk.redditbot.connectors;


import org.joda.time.DateTime;
import pl.jpetryk.redditbot.exceptions.TwitterApiException;
import pl.jpetryk.redditbot.model.Tweet;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Jan on 09/01/15.
 */
public class Twitter4JConnector implements TwitterConnectorInterface {

    private Twitter twitter;

    private Twitter4JConnector(Builder builder) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(builder.apiKey);
        configurationBuilder.setOAuthConsumerSecret(builder.apiSecret);
        configurationBuilder.setOAuthAccessToken(builder.accessToken);
        configurationBuilder.setOAuthAccessTokenSecret(builder.accessTokenSecret);
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
                    e.getRateLimitStatus().getSecondsUntilReset() > 0 ?
                            e.getRateLimitStatus().getSecondsUntilReset() * 1000 : 0, e.getErrorCode());
        }
    }

    private void prepareEntities(Status status, Tweet.Builder tweetBuilder) {
        for (URLEntity urlEntity : status.getURLEntities()) {
            tweetBuilder.addUrlEntity(urlEntity.getURL(), urlEntity.getExpandedURL());
        }
        for (MediaEntity mediaEntity : status.getMediaEntities()) {
            tweetBuilder.addMediaEntity(mediaEntity.getURL(), mediaEntity.getMediaURL());
        }
    }


    public static class Builder {
        private String apiKey;
        private String apiSecret;
        private String accessToken;
        private String accessTokenSecret;

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder apiSecret(String apiSecret) {
            this.apiSecret = apiSecret;
            return this;
        }

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder accessTokenSecret(String accessTokenSecret) {
            this.accessTokenSecret = accessTokenSecret;
            return this;
        }

        public Twitter4JConnector build() {
            return new Twitter4JConnector(this);
        }

    }


}

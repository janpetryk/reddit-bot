package pl.jpetryk.redditbot.bot.impl;

import org.json.JSONException;
import pl.jpetryk.redditbot.bot.AbstractRedditBot;
import pl.jpetryk.redditbot.connectors.ImgurConnectorInterface;
import pl.jpetryk.redditbot.connectors.RedditConnectorInterface;
import pl.jpetryk.redditbot.connectors.TwitterConnectorInterface;
import pl.jpetryk.redditbot.exceptions.TwitterApiException;
import pl.jpetryk.redditbot.model.*;
import pl.jpetryk.redditbot.utils.CommentParser;
import pl.jpetryk.redditbot.utils.ResponseCommentCreatorInterface;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Jan on 10/01/15.
 */
public class TweetsInCommentsBot extends AbstractRedditBot {

    private CommentParser commentParser;

    private TwitterConnectorInterface twitterConnector;

    private ResponseCommentCreatorInterface responseCommentCreator;

    private List<String> userNameBlackList;

    private ImgurConnectorInterface imgurConnector;

    @Inject
    public TweetsInCommentsBot(TwitterConnectorInterface twitterConnector,
                               RedditConnectorInterface redditConnectorInterface,
                               ImgurConnectorInterface imgurConnector,
                               CommentParser commentParser,
                               @Named("subreddits") String subreddits,
                               ResponseCommentCreatorInterface responseCommentCreator,
                               List<String> userNameBlackList) {
        super(redditConnectorInterface, subreddits);
        this.twitterConnector = twitterConnector;
        this.imgurConnector = imgurConnector;
        this.commentParser = commentParser;
        this.responseCommentCreator = responseCommentCreator;
        this.userNameBlackList = new ArrayList<>();
        for (String string : userNameBlackList) {
            this.userNameBlackList.add(string.toLowerCase());
        }
    }

    @Override
    protected ProcessCommentResult processComment(Comment comment) throws Exception {
        ProcessCommentResult result;
        if (userNameBlackList.contains(comment.getAuthor().toLowerCase())) {
            result = ProcessCommentResult.doNotRespond();
        } else {
            try {
                List<String> statusIdList = commentParser.getRegexGroup(comment, 3);
                List<Tweet> tweetList = readTweets(statusIdList);
                List<Tweet> filteredTweetList = filterOutTweetsThatAreAlreadyInComment(comment, tweetList);
                if (filteredTweetList.isEmpty()) {
                    result = ProcessCommentResult.doNotRespond();
                } else {
                    String response = responseCommentCreator.
                            createResponseComment(getTweetWithRehostedImagesList(filteredTweetList), comment);
                    result = ProcessCommentResult.respondWith(response);
                }
            } catch (TwitterApiException e) {
                result = handleException(comment, e);
            }
        }
        return result;
    }

    private List<TweetWithRehostedImages> getTweetWithRehostedImagesList(List<Tweet> tweetList) {
        List<TweetWithRehostedImages> result = new ArrayList<>();
        for (Tweet tweet : tweetList) {
            result.add(new TweetWithRehostedImages(tweet, rehostTweetImages(tweet)));
        }
        return result;
    }

    private List<RehostedImageEntity> rehostTweetImages(Tweet tweet) {
        List<RehostedImageEntity> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : tweet.getImageEntities().entrySet()) {
            String rehostedImageUrl = null;
            try {
                rehostedImageUrl = imgurConnector.reuploadImage(entry.getValue());
            } catch (IOException | JSONException e) {
                logger.error(e.getMessage(), e);
            }
            result.add(new RehostedImageEntity(entry.getKey(), entry.getValue(), rehostedImageUrl));
        }
        return result;
    }

    private List<Tweet> readTweets(List<String> statusIdList) throws TwitterApiException {
        List<Tweet> tweetList = new ArrayList<>();
        for (String string : statusIdList) {
            tweetList.add(twitterConnector.showStatus(Long.valueOf(string)));
        }
        return tweetList;
    }

    private List<Tweet> filterOutTweetsThatAreAlreadyInComment(Comment comment, List<Tweet> tweetList){
        List<Tweet> result= new ArrayList<>();
        for (Tweet tweet : tweetList) {
            if (!comment.getBody().contains(tweet.getBody())) {
                result.add(tweet);
            }
        }
        return result;
    }

    private ProcessCommentResult handleException(Comment comment, TwitterApiException e) throws Exception {
        if (e.isRateLimitExceeded()) {
            logger.warn("Twitter rate limit exceeded. Sleeping for " + e.getMiliSecondsUntilReset() / 1000 + " seconds");
            sleepUntilRateLimitEnds(e.getMiliSecondsUntilReset());
            return processComment(comment);
        } else {
            logger.error("Twitter api error occured. Message: " + e.getMessage() + ". Error code: " + e.getErrorCode()
                    + " Comment removed from response queue");
            return ProcessCommentResult.doNotRespond();
        }
    }

    private void sleepUntilRateLimitEnds(Long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            logger.error(e);
        }
    }

}

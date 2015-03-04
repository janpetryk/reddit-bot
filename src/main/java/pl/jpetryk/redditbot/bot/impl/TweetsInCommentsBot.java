package pl.jpetryk.redditbot.bot.impl;

import org.json.JSONException;
import pl.jpetryk.redditbot.bot.AbstractRedditBot;
import pl.jpetryk.redditbot.connectors.ImgurConnectorInterface;
import pl.jpetryk.redditbot.connectors.RedditConnectorInterface;
import pl.jpetryk.redditbot.connectors.TwitterConnectorInterface;
import pl.jpetryk.redditbot.exceptions.TwitterApiException;
import pl.jpetryk.redditbot.model.*;
import pl.jpetryk.redditbot.utils.CommentParser;
import pl.jpetryk.redditbot.utils.ResponseCommentCreator;

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

    private ResponseCommentCreator responseCommentCreator;

    private List<String> userNameBlackList;

    private ImgurConnectorInterface imgurConnector;

    @Inject
    public TweetsInCommentsBot(TwitterConnectorInterface twitterConnector,
                               RedditConnectorInterface redditConnectorInterface,
                               ImgurConnectorInterface imgurConnector,
                               CommentParser commentParser,
                               @Named("subreddits") String subreddits,
                               ResponseCommentCreator responseCommentCreator,
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
        if (userNameBlackList.contains(comment.getAuthor().toLowerCase())) {
            return ProcessCommentResult.doNotRespond();
        } else {
            try {
                List<String> statusIdList = commentParser.getRegexGroup(comment, 3);
                List<Tweet> tweetList = getTweetsThatAreNotAlreadyInComment(comment, statusIdList);
                if (tweetList.isEmpty()) {
                    return ProcessCommentResult.doNotRespond();
                } else {
                    String response = responseCommentCreator.
                            createResponseComment(getTweetWithRehostedImagesesList(tweetList));
                    if (isTrashTalkThread(comment)) {
                        response = response.toUpperCase();
                    }
                    return ProcessCommentResult.respondWith(response);
                }
            } catch (TwitterApiException e) {
                return handleException(comment, e);
            }
        }
    }

    private List<TweetWithRehostedImages> getTweetWithRehostedImagesesList(List<Tweet> tweetList) {
        List<TweetWithRehostedImages> result = new ArrayList<>();
        for (Tweet tweet : tweetList) {
            result.add(new TweetWithRehostedImages(tweet, getRehostedImages(tweet)));
        }

        return result;
    }

    private List<RehostedImageEntity> getRehostedImages(Tweet tweet) {
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


    private boolean isTrashTalkThread(Comment comment) {
        return comment.getLinkTitle().contains("TRASH TALK THREAD")
                || comment.getLinkTitle().contains("THRASHTALK THREAD");
    }

    private List<Tweet> getTweetsThatAreNotAlreadyInComment(Comment comment, List<String> statusIdList)
            throws Exception {
        List<Tweet> tweetList = new ArrayList<>();
        for (String string : statusIdList) {
            Tweet tweet = twitterConnector.showStatus(Long.valueOf(string));
            if (!comment.getBody().contains(tweet.getBody())) {
                tweetList.add(tweet);
            }
        }
        return tweetList;
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

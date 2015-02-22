package pl.jpetryk.redditbot.bot.impl;

import org.json.JSONException;
import pl.jpetryk.redditbot.bot.AbstractRedditBot;
import pl.jpetryk.redditbot.connectors.ImgurConnector;
import pl.jpetryk.redditbot.connectors.ImgurConnectorInterface;
import pl.jpetryk.redditbot.connectors.RedditConnectorInterface;
import pl.jpetryk.redditbot.connectors.TwitterConnectorInterface;
import pl.jpetryk.redditbot.exceptions.TwitterApiException;
import pl.jpetryk.redditbot.model.Comment;
import pl.jpetryk.redditbot.model.ImageEntity;
import pl.jpetryk.redditbot.model.ProcessCommentResult;
import pl.jpetryk.redditbot.model.Tweet;
import pl.jpetryk.redditbot.utils.CommentParser;
import pl.jpetryk.redditbot.utils.ResponseCommentCreator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

    public TweetsInCommentsBot(TwitterConnectorInterface twitterConnector,
                               RedditConnectorInterface redditConnectorInterface,
                               ImgurConnectorInterface imgurConnector,
                               CommentParser commentParser,
                               String subreddits,
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
        try {
            List<String> statusIdList = commentParser.getRegexGroup(comment, 3);
            List<Tweet> tweetList = getTweetsThatAreNotAlreadyInComment(comment, statusIdList);
            if (tweetList.isEmpty() || userNameBlackList.contains(comment.getAuthor().toLowerCase())) {
                return ProcessCommentResult.doNotRespond();
            } else {
                String response = responseCommentCreator.createResponseComment(tweetList);
                if (isTrashTalkThread(comment)) {
                    response = response.toUpperCase();
                }
                return ProcessCommentResult.respondWith(response);
            }
        } catch (TwitterApiException e) {
            return handleException(comment, e);
        }
    }

    private void rehostImages(List<ImageEntity> imageEntities) {
        for (ImageEntity imageEntity : imageEntities) {
            String reuploadedImageUrl = null;
            try {
                reuploadedImageUrl = imgurConnector.reuploadImage(imageEntity.getExpandedUrl());
            } catch (Exception e) {
            }
            imageEntity.setRehostedUrl(reuploadedImageUrl);
        }
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
                rehostImages(tweet.getImageEntities());
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

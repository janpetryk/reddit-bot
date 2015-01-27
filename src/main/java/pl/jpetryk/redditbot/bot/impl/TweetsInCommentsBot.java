package pl.jpetryk.redditbot.bot.impl;

import pl.jpetryk.redditbot.bot.AbstractRedditBot;
import pl.jpetryk.redditbot.connectors.RedditConnectorInterface;
import pl.jpetryk.redditbot.connectors.TwitterConnectorInterface;
import pl.jpetryk.redditbot.exceptions.TwitterApiException;
import pl.jpetryk.redditbot.model.Comment;
import pl.jpetryk.redditbot.model.ProcessCommentResult;
import pl.jpetryk.redditbot.model.Tweet;
import pl.jpetryk.redditbot.utils.CommentParser;
import pl.jpetryk.redditbot.utils.ResponseCommentCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan on 10/01/15.
 */
public class TweetsInCommentsBot extends AbstractRedditBot {

    private CommentParser commentParser;

    private TwitterConnectorInterface twitterConnector;

    private ResponseCommentCreator responseCommentCreator;

    private List<String> userNameBlackList;

    public TweetsInCommentsBot(TwitterConnectorInterface twitterConnectorInterface,
                               RedditConnectorInterface redditConnectorInterface,
                               CommentParser commentParser,
                               String subreddits,
                               ResponseCommentCreator responseCommentCreator,
                               List<String> userNameBlackList) {
        super(redditConnectorInterface, subreddits);
        twitterConnector = twitterConnectorInterface;
        this.commentParser = commentParser;
        this.responseCommentCreator = responseCommentCreator;
        this.userNameBlackList = new ArrayList<>();
        for (String string : userNameBlackList) {
            this.userNameBlackList.add(string.toLowerCase());
        }
    }


//    @Override
//    protected boolean shouldRespondToComment(Comment comment) throws Exception {
//        return commentParser.commentMatchesRegex(comment)
//                && !userNameBlackList.contains(comment.getAuthor().toLowerCase());
//    }
//
//    @Override
//    protected String responseMessage(Comment comment) throws Exception {
//        try {
//            List<Tweet> tweetList = new ArrayList<>();
//            for (String string : commentParser.getRegexGroup(comment, 3)) {
//                tweetList.add(twitterConnector.showStatus(Long.valueOf(string)));
//            }
//            return responseCommentCreator.createResponseComment(tweetList);
//        } catch (TwitterApiException e) {
//            if (e.isRateLimitExceeded()) {
//                sleepUntilRateLimitEnds(e.getMiliSecondsUntilReset());
//                return responseMessage(comment);
//            }
//            throw e;
//        }
//    }

    @Override
    protected ProcessCommentResult processComment(Comment comment) throws Exception {
        try {
            List<String> statusIdList = commentParser.getRegexGroup(comment, 3);
            if (statusIdList.isEmpty() || userNameBlackList.contains(comment.getAuthor().toLowerCase())) {
                return ProcessCommentResult.doNotRespond();
            } else {
                List<Tweet> tweetList = getTweetsThatAreNotAlreadyInComment(comment, statusIdList);
                if (tweetList.isEmpty()) {
                    return ProcessCommentResult.doNotRespond();
                } else {
                    return ProcessCommentResult.respondWith(responseCommentCreator.createResponseComment(tweetList));
                }
            }
        } catch (TwitterApiException e) {
            return handleException(comment, e);
        }
    }

    private List<Tweet> getTweetsThatAreNotAlreadyInComment(Comment comment, List<String> statusIdList) throws TwitterApiException {
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

package pl.jpetryk.redditbot.bot.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import pl.jpetryk.redditbot.bot.AbstractRedditBot;
import pl.jpetryk.redditbot.connectors.ImgurConnectorInterface;
import pl.jpetryk.redditbot.connectors.RedditConnectorInterface;
import pl.jpetryk.redditbot.connectors.TwitterConnectorInterface;
import pl.jpetryk.redditbot.exceptions.ImgurException;
import pl.jpetryk.redditbot.exceptions.TwitterApiException;
import pl.jpetryk.redditbot.model.Comment;
import pl.jpetryk.redditbot.model.ProcessCommentResult;
import pl.jpetryk.redditbot.model.RehostedImageEntity;
import pl.jpetryk.redditbot.model.Tweet;
import pl.jpetryk.redditbot.model.TweetWithRehostedImages;
import pl.jpetryk.redditbot.parser.CommentParser;
import pl.jpetryk.redditbot.utils.ResponseCommentCreatorInterface;

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
		if (userNameBlackList.contains(comment.getAuthor().toLowerCase())) {
			return ProcessCommentResult.doNotRespond();
		}
		try {
			Collection<String> statusIdList = commentParser.getTwitterStatusIdsFromComment(comment);
			List<Tweet> tweetList = readTweets(statusIdList);
			List<Tweet> filteredTweetList = filterOutTweetsThatAreAlreadyInComment(comment, tweetList);
			if (filteredTweetList.isEmpty()) {
				return ProcessCommentResult.doNotRespond();
			}
			String response = responseCommentCreator.
				createResponseComment(getTweetWithRehostedImagesList(filteredTweetList), comment);
			return ProcessCommentResult.respondWith(response);
		} catch (TwitterApiException e) {
			return handleException(comment, e);
		}
	}

	private List<TweetWithRehostedImages> getTweetWithRehostedImagesList(List<Tweet> tweetList) {
		return tweetList.stream()
			.map(tweet -> new TweetWithRehostedImages(tweet, rehostTweetImages(tweet)))
			.collect(Collectors.toList());
	}

	private Multimap<String, RehostedImageEntity> rehostTweetImages(Tweet tweet) {
		Multimap<String, RehostedImageEntity> result = HashMultimap.create();
		try {
			for (Map.Entry<String, Collection<String>> entry : tweet.getImageEntities().asMap().entrySet()) {
				for (String originalUrl : entry.getValue()) {
					result.put(entry.getKey(), new RehostedImageEntity(originalUrl, imgurConnector.reuploadImage(originalUrl)));
				}
			}
		} catch (ImgurException e) {
			logger.error(e.getMessage(), e);
		}
		return result;
	}

	private List<Tweet> readTweets(Collection<String> statusIdList) throws TwitterApiException {
		return statusIdList.stream()
			.map(Long::valueOf)
			.map(twitterConnector::showStatus)
			.collect(Collectors.toList());
	}

	private List<Tweet> filterOutTweetsThatAreAlreadyInComment(Comment comment, List<Tweet> tweetList) {
		return tweetList.stream()
			.filter(tweet -> !comment.getBody().contains(tweet.getBody()))
			.collect(Collectors.toList());
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

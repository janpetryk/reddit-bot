package pl.jpetryk.redditbot.bot.impl;

import pl.jpetryk.redditbot.bot.AbstractRedditBot;
import pl.jpetryk.redditbot.connectors.RedditConnectorInterface;
import pl.jpetryk.redditbot.connectors.TwitterConnectorInterface;
import pl.jpetryk.redditbot.model.Comment;
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


    public TweetsInCommentsBot(TwitterConnectorInterface twitterConnectorInterface,
                               RedditConnectorInterface redditConnectorInterface,
                               CommentParser commentParser,
                               String subreddits, ResponseCommentCreator responseCommentCreator) {
        super(redditConnectorInterface, subreddits);
        twitterConnector = twitterConnectorInterface;
        this.commentParser = commentParser;
        this.responseCommentCreator = responseCommentCreator;
    }


    @Override
    protected boolean shouldRespondToComment(Comment comment) throws Exception {
        return commentParser.commentMatchesRegex(comment);
    }

    @Override
    protected List<String> responseMessages(Comment comment) throws Exception {
        List<String> result = new ArrayList<>();
        for (String string : commentParser.getRegexGroup(comment, 3)) {
            result.add(responseCommentCreator.createResponseComment(twitterConnector.showStatus(Long.valueOf(string))));
        }
        return result;
    }
}

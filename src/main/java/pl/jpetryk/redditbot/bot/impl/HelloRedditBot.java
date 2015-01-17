package pl.jpetryk.redditbot.bot.impl;

import pl.jpetryk.redditbot.bot.AbstractRedditBot;
import pl.jpetryk.redditbot.connectors.RedditConnectorInterface;
import pl.jpetryk.redditbot.model.Comment;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Jan on 06/01/15.
 */
public class HelloRedditBot extends AbstractRedditBot {

    public HelloRedditBot(RedditConnectorInterface redditConnectorInterface, String subreddits) {
        super(redditConnectorInterface, subreddits);
    }

    @Override
    protected boolean shouldRespondToComment(Comment comment) throws Exception {
        return comment.getBody().contains("Hello!");
    }

    @Override
    protected List<String> responseMessages(Comment comment) throws Exception {
        return Arrays.asList("Hello " + comment.getAuthor() + "!");
    }

}

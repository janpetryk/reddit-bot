package pl.jpetryk.redditbot.bot.impl;

import pl.jpetryk.redditbot.bot.AbstractRedditBot;
import pl.jpetryk.redditbot.connectors.RedditConnectorInterface;
import pl.jpetryk.redditbot.model.Comment;
import pl.jpetryk.redditbot.model.ProcessCommentResult;

/**
 * Created by Jan on 06/01/15.
 */
public class HelloRedditBot extends AbstractRedditBot {

    public HelloRedditBot(RedditConnectorInterface redditConnectorInterface, String subreddits, String botUserName) {
        super(redditConnectorInterface, subreddits);
    }

    @Override
    protected ProcessCommentResult processComment(Comment comment) throws Exception {
        if (comment.getBody().contains("Hello!")) {
            return ProcessCommentResult.respondWith("Hello, " + comment.getAuthor());
        } else {
            return ProcessCommentResult.doNotRespond();
        }
    }

//    @Override
//    protected boolean shouldRespondToComment(Comment comment) throws Exception {
//        return comment.getBody().contains("Hello!");
//    }
//
//    @Override
//    protected String responseMessage(Comment comment) throws Exception {
//        return "Hello " + comment.getAuthor() + "!";
//    }

}

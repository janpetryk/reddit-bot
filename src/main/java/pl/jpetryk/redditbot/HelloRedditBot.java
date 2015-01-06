package pl.jpetryk.redditbot;

import pl.jpetryk.redditbot.model.Comment;
import pl.jpetryk.redditbot.model.Configuration;

/**
 * Created by Jan on 06/01/15.
 */
public class HelloRedditBot extends AbstractRedditBot {

    public HelloRedditBot(Configuration configuration){
        super(configuration);
    }

    @Override
    public boolean shouldRespondToComment(Comment comment) {
        return "Hello!".equals(comment.getBody());
    }

    @Override
    public String responseMessage(Comment comment) {
        return "Hello " + comment.getAuthor()+"!";
    }
}

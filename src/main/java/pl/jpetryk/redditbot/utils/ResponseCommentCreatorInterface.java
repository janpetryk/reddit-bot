package pl.jpetryk.redditbot.utils;

import pl.jpetryk.redditbot.model.Comment;
import pl.jpetryk.redditbot.model.TweetWithRehostedImages;

import java.util.List;

/**
 * Created by Jan on 15/03/15.
 */
public interface ResponseCommentCreatorInterface {

    public String createResponseComment(List<TweetWithRehostedImages> tweetList, Comment comment);

}

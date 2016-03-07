package pl.jpetryk.redditbot.parser;

import pl.jpetryk.redditbot.model.Comment;

import java.util.Collection;

/**
 * Created by Jan on 2015-12-30.
 */
public interface CommentParser {

    Collection<String> getTwitterStatusIdsFromComment(Comment comment);

}
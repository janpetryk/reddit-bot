package pl.jpetryk.redditbot.mock.parser;

import com.google.inject.Singleton;
import pl.jpetryk.redditbot.model.Comment;
import pl.jpetryk.redditbot.parser.CommentParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Jan on 2015-12-30.
 */
@Singleton
public class CommentParserMock implements CommentParser {

    private Collection<String> matchingCommentCollection = new ArrayList<>();


    @Override
    public Collection<String> getTwitterStatusIdsFromComment(Comment comment) {
        List<String> result = new ArrayList<>();
        if (Integer.valueOf(comment.getCommentId()) % 10 == 0) {
            result.add(comment.getCommentId());
            matchingCommentCollection.add(comment.getCommentFullName());
        }
        return result;
    }

    public Collection<String> getMatchingCommentFullIdCollection() {
        return matchingCommentCollection;
    }
}

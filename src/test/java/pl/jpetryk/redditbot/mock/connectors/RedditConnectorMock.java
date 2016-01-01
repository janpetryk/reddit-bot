package pl.jpetryk.redditbot.mock.connectors;

import com.google.inject.Singleton;
import org.joda.time.DateTime;
import pl.jpetryk.redditbot.connectors.RedditConnectorInterface;
import pl.jpetryk.redditbot.exceptions.NetworkConnectionException;
import pl.jpetryk.redditbot.exceptions.RedditApiException;
import pl.jpetryk.redditbot.model.Comment;
import pl.jpetryk.redditbot.model.PostCommentResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by Jan on 2015-12-30.
 */
@Singleton
public class RedditConnectorMock implements RedditConnectorInterface {

    private Collection<String> responseCommentFullIdCollection = new ArrayList<>();
    private int counter = 0;

    @Override
    public List<Comment> getNewestSubredditComments(String subredditName) throws NetworkConnectionException {
        sleep();
        List<Comment> result = new ArrayList<>();
        int limit = counter + 100;
        for (; counter < limit; counter++) {
            result.add(prepareComment(String.valueOf(counter)));
        }
        counter = counter - 10;
        return result;
    }

    @Override
    public PostCommentResult replyToComment(String parentCommentFullName, String responseCommentBody) throws NetworkConnectionException, RedditApiException {
        sleep();
        responseCommentFullIdCollection.add(parentCommentFullName);
        return PostCommentResult.successful("asda");
    }

    private void sleep() {
        try {
            Thread.sleep(50L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Comment prepareComment(String counterString) {
        return new Comment.Builder()
                .commentId(counterString)
                .author("author" + counterString)
                .created(new DateTime())
                .body("body" + counterString)
                .linkId("linkId" + counterString)
                .linkUrl("linkUrl" + counterString)
                .linktTitle("linkTitle" + counterString)
                .subreddit("subreddit" + counterString)
                .build();
    }

    public Collection<String> getRespondedToCommentsFullIdCollection() {
        return responseCommentFullIdCollection;
    }
}

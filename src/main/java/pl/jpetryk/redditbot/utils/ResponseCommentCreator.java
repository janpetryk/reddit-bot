package pl.jpetryk.redditbot.utils;

import com.google.inject.Inject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import pl.jpetryk.redditbot.model.Comment;
import pl.jpetryk.redditbot.model.RehostedImageEntity;
import pl.jpetryk.redditbot.model.TweetWithRehostedImages;

import javax.inject.Named;
import java.util.List;
import java.util.Map;

/**
 * Created by Jan on 17/01/15.
 */
public class ResponseCommentCreator implements ResponseCommentCreatorInterface {

    private String tweetResponseTemplate;
    private String footerTemplate;
    private String datePattern;
    private String twitterPicLinkTemplate;
    private String imgurPicLinkTemplate;

    @Inject
    public ResponseCommentCreator(@Named("response-template") String tweetResponseTemplate,
                                  @Named("footer-template") String footerTemplate,
                                  @Named("date-pattern") String datePattern,
                                  @Named("twitter-pic-link") String twitterPicLink,
                                  @Named("imgur-pic-link") String imgurPicLink) {
        this.tweetResponseTemplate = tweetResponseTemplate;
        this.footerTemplate = footerTemplate;
        this.datePattern = datePattern;
        this.twitterPicLinkTemplate = twitterPicLink;
        this.imgurPicLinkTemplate = imgurPicLink;

    }

    @Override
    public String createResponseComment(List<TweetWithRehostedImages> tweetList, Comment comment) {
        String result = "";
        for (TweetWithRehostedImages tweet : tweetList) {
            result += createResponseComment(tweet);
        }
        result += footerTemplate;
        return result;
    }

    private String createResponseComment(TweetWithRehostedImages tweet) {
        StringBuilder result = new StringBuilder(tweetResponseTemplate);
        replaceAll(result, "${posterScreenName}", escapeRedditSpecialCharacters(tweet.getPosterScreenName()));
        replaceAll(result, "${posterProfileUrl}", tweet.getPosterProfileUrl());
        replaceAll(result, "${datePosted}",
                convertDateToString(tweet.getDatePosted()));
        replaceAll(result, "${tweetUrl}", tweet.getTweetUrl());
        replaceAll(result, "${body}", prepareTweetBody(tweet));
        return result.toString();
    }

    private String prepareTweetBody(TweetWithRehostedImages tweet) {
        StringBuilder tweetStringBuilder = new StringBuilder(escapeRedditSpecialCharacters(tweet.getBody()));
        for (RehostedImageEntity imageEntity : tweet.getRehostedImageEntityList()) {
            replaceAll(tweetStringBuilder, imageEntity.getUrl(), getImageLinks(imageEntity));
        }
        for (Map.Entry<String, String> entry : tweet.getUrlEntities().entrySet()) {
            replaceAll(tweetStringBuilder, entry.getKey(), getNoParticipationRedditLink(entry.getValue()));
        }
        return tweetStringBuilder.toString();
    }



    private String getImageLinks(RehostedImageEntity entity) {
        String result = createRedditHyperLink(entity.getExpandedUrl(), twitterPicLinkTemplate);
        if (entity.getRehostedUrl() != null) {
            result = result + " " + createRedditHyperLink(entity.getRehostedUrl(), imgurPicLinkTemplate);
        }
        return result;
    }

    private String createRedditHyperLink(String source, String name) {
        return "[" + name + "]" + "(" + source + ")";
    }

    private String convertDateToString(DateTime dateTime) {
        return dateTime.toDateTime(DateTimeZone.UTC).toString(datePattern);
    }

    private String getNoParticipationRedditLink(String url) {
        return url.replace("reddit.com", "np.reddit.com");
    }

    private boolean isTrashTalkThread(Comment comment) {
        return comment.getLinkTitle().contains("TRASH TALK THREAD")
                || comment.getLinkTitle().contains("THRASHTALK THREAD");
    }

    private String escapeRedditSpecialCharacters(String string) {
        StringBuilder tweetStringBuilder = new StringBuilder(string);
        replaceAll(tweetStringBuilder, ">", "\\>");
        replaceAll(tweetStringBuilder, "\n", "\n\n> ");
        replaceAll(tweetStringBuilder, "#", "\\#");
        replaceAll(tweetStringBuilder, "^", "\\^");
        replaceAll(tweetStringBuilder, "*", "\\*");
        replaceAll(tweetStringBuilder, "_", "\\_");
        return tweetStringBuilder.toString();
    }

    private void replaceAll(StringBuilder builder, String from, String to) {
        int index = builder.indexOf(from);
        while (index != -1) {
            builder.replace(index, index + from.length(), to);
            index += to.length();
            index = builder.indexOf(from, index);
        }
    }

}

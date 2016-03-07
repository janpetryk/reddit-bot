package pl.jpetryk.redditbot.bot;

import com.google.inject.Inject;
import org.junit.Assert;
import org.junit.runner.RunWith;
import pl.jpetryk.redditbot.GuiceJUnitRunner;
import pl.jpetryk.redditbot.TestModule;
import pl.jpetryk.redditbot.mock.connectors.RedditConnectorMock;
import pl.jpetryk.redditbot.mock.parser.CommentParserMock;
import pl.jpetryk.redditbot.utils.PropertiesReader;

/**
 * Created by Jan on 2015-12-30.
 */
@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules({TestModule.class})
public class AbstractRedditBotTest {

    private AbstractRedditBot bot;
    private RedditConnectorMock redditConnectorMock;
    private CommentParserMock commentParserMock;

    @org.junit.Test
    public void test() throws Exception {
        PropertiesReader redditProperties = new PropertiesReader("resources/bot.properties");
        for (int i = 0; i < 5; i++) {
            bot.run();
            Thread.sleep(Long.valueOf(redditProperties.getProperty("run-interval")));
        }
        Thread.sleep(Long.valueOf(redditProperties.getProperty("run-interval")));
        Assert.assertEquals(commentParserMock.getMatchingCommentFullIdCollection(),
                redditConnectorMock.getRespondedToCommentsFullIdCollection());

    }

    @Inject
    public void setBot(AbstractRedditBot bot) {
        this.bot = bot;
    }

    @Inject
    public void setRedditConnectorMock(RedditConnectorMock redditConnectorMock) {
        this.redditConnectorMock = redditConnectorMock;
    }

    @Inject
    public void setCommentParserMock(CommentParserMock commentParserMock) {
        this.commentParserMock = commentParserMock;
    }
}

package pl.jpetryk.redditbot;

import com.google.common.io.Files;
import org.apache.log4j.*;
import pl.jpetryk.redditbot.bot.AbstractRedditBot;
import pl.jpetryk.redditbot.bot.impl.TweetsInCommentsBot;
import pl.jpetryk.redditbot.connectors.JrawRedditConnector;
import pl.jpetryk.redditbot.connectors.RedditConnectorInterface;
import pl.jpetryk.redditbot.connectors.Twitter4JConnector;
import pl.jpetryk.redditbot.connectors.TwitterConnectorInterface;
import pl.jpetryk.redditbot.utils.CommentParser;
import pl.jpetryk.redditbot.utils.PropertiesReader;
import pl.jpetryk.redditbot.utils.ResponseCommentCreator;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by Jan on 06/01/15.
 */
public class Runner {

    private static final Logger logger = Logger.getLogger(Runner.class);

    public static void main(String[] args) throws Exception {

        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.TRACE);
        rootLogger.addAppender(new FileAppender(
                new PatternLayout("%-6r [%p] %c - %m%n"), "bot.log"));

        PropertiesReader redditProperties = new PropertiesReader("resources/bot.properties");

        RedditConnectorInterface redditConnector = new JrawRedditConnector.Builder()
                .login(redditProperties.getProperty("reddit-login"))
                .password(redditProperties.getProperty("reddit-password"))
                .clientId(redditProperties.getProperty("reddit-client-id"))
                .clientSecret(redditProperties.getProperty("reddit-client-secret"))
                .userAgent(redditProperties.getProperty("reddit-useragent"))
                .build();
        logger.trace("created reddit connector");

        PropertiesReader twitterProperties = new PropertiesReader("resources/twitter.properties");

        TwitterConnectorInterface twitterConnector = new Twitter4JConnector.Builder()
                .apiKey(twitterProperties.getProperty("api-key"))
                .apiSecret(twitterProperties.getProperty("api-secret"))
                .accessToken(twitterProperties.getProperty("access-token"))
                .accessTokenSecret(twitterProperties.getProperty("access-token-secret"))
                .build();

        logger.trace("created twitter connector");

        CommentParser parser = new CommentParser(twitterProperties.getProperty("twitter-status-regex"));
        logger.trace("created comment parser");

        String responseTemplate = Files.toString(new File("resources/reply-template"), Charset.defaultCharset());
        String footerTemplate = Files.toString(new File("resources/footer-template"), Charset.defaultCharset());
        ResponseCommentCreator responseCommentCreator = new ResponseCommentCreator(responseTemplate, footerTemplate);
        logger.trace("created response comment creator");

        AbstractRedditBot bot = new TweetsInCommentsBot(twitterConnector, redditConnector, parser,
                redditProperties.getProperty("subreddits"),
                responseCommentCreator, Arrays.asList(redditProperties.getProperty("reddit-login"), "TweetPoster"));
        logger.trace("created bot");


        while (true) {
            bot.run();
            Thread.sleep(1000);
        }
    }


}

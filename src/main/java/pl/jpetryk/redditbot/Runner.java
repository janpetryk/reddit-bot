package pl.jpetryk.redditbot;

import com.google.common.io.Files;
import org.apache.log4j.*;
import pl.jpetryk.redditbot.bot.AbstractRedditBot;
import pl.jpetryk.redditbot.bot.impl.TweetsInCommentsBot;
import pl.jpetryk.redditbot.connectors.*;
import pl.jpetryk.redditbot.exceptions.NetworkConnectionException;
import pl.jpetryk.redditbot.exceptions.RedditApiException;
import pl.jpetryk.redditbot.utils.CommentParser;
import pl.jpetryk.redditbot.utils.PropertiesReader;
import pl.jpetryk.redditbot.utils.ResponseCommentCreator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jan on 06/01/15.
 */
public class Runner {

    private static final Logger logger = Logger.getLogger(Runner.class);

    public static void main(String[] args) throws Exception {


        PropertiesReader redditProperties = new PropertiesReader("resources/bot.properties");
        PropertiesReader twitterProperties = new PropertiesReader("resources/twitter.properties");
        PropertiesReader templateProperties = new PropertiesReader("resources/template/template.properties");


        configureLogger();

        RedditConnectorInterface redditConnector = prepareRedditConnector(redditProperties);
        logger.trace("created reddit connector");

        TwitterConnectorInterface twitterConnector = prepareTwitterConnector(twitterProperties);

        logger.trace("created twitter connector");

        CommentParser parser = new CommentParser(twitterProperties.getProperty("twitter-status-regex"));
        logger.trace("created comment parser");

        ImgurConnectorInterface imgurConnector = getImgurConnector(redditProperties);
        logger.trace("created imgur connector");


        ResponseCommentCreator responseCommentCreator = prepareResponseCommentCreator(templateProperties);
        logger.trace("created response comment creator");

        AbstractRedditBot bot = prepareBot(redditProperties, redditConnector, imgurConnector, twitterConnector,
                parser, responseCommentCreator);
        logger.trace("created bot");

        while (true) {
            bot.run();
            Thread.sleep(Long.valueOf(redditProperties.getProperty("run-interval")));
        }
    }

    private static AbstractRedditBot prepareBot(PropertiesReader redditProperties,
                                                RedditConnectorInterface redditConnector,
                                                ImgurConnectorInterface imgurConnector,
                                                TwitterConnectorInterface twitterConnector,
                                                CommentParser parser,
                                                ResponseCommentCreator responseCommentCreator) {
        List<String> blacklist = new ArrayList<>(Arrays.asList(redditProperties.getProperty("blacklist").split(", ")));
        blacklist.add(redditProperties.getProperty("reddit-login"));
        return new TweetsInCommentsBot(twitterConnector, redditConnector, imgurConnector, parser,
                redditProperties.getProperty("subreddits"), responseCommentCreator, blacklist);
    }

    private static ResponseCommentCreator prepareResponseCommentCreator(PropertiesReader templateProperties)
            throws IOException {
        String responseTemplate = Files.toString(new File("resources/template/reply-template"),
                Charset.defaultCharset());
        String footerTemplate = Files.toString(new File("resources/template/footer-template"),
                Charset.defaultCharset());
        return new ResponseCommentCreator(responseTemplate, footerTemplate,
                templateProperties.getProperty("date-pattern"), templateProperties.getProperty("twitter-pic-link"),
                templateProperties.getProperty("imgur-pic-link"));
    }

    private static ImgurConnector getImgurConnector(PropertiesReader botProperties) {
        return new ImgurConnector(botProperties.getProperty("imgur-client-id"),
                botProperties.getProperty("imgur-client-secret"));
    }

    private static Twitter4JConnector prepareTwitterConnector(PropertiesReader twitterProperties) {
        return new Twitter4JConnector(twitterProperties.getProperty("api-key"),
                twitterProperties.getProperty("api-secret"),
                twitterProperties.getProperty("access-token"),
                twitterProperties.getProperty("access-token-secret"));
    }

    private static JrawRedditConnector prepareRedditConnector(PropertiesReader redditProperties) throws NetworkConnectionException, RedditApiException {

        return new JrawRedditConnector(redditProperties.getProperty("reddit-useragent"),
                redditProperties.getProperty("reddit-login"),
                redditProperties.getProperty("reddit-password"),
                redditProperties.getProperty("reddit-client-id"),
                redditProperties.getProperty("reddit-client-secret"));
    }

    private static void configureLogger() throws IOException {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.WARN);
        rootLogger.addAppender(new FileAppender(
                new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} [%p] %c - %m%n"), "bot.log"));
    }


}

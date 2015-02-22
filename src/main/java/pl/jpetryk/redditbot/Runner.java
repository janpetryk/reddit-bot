package pl.jpetryk.redditbot;

import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Injector;
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

    public static void main(String[] args) throws Exception {


        PropertiesReader redditProperties = new PropertiesReader("resources/bot.properties");

        configureLogger();
        Injector injector = Guice.createInjector(new BotModule());

        AbstractRedditBot bot = injector.getInstance(TweetsInCommentsBot.class);

        while (true) {
            bot.run();
            Thread.sleep(Long.valueOf(redditProperties.getProperty("run-interval")));
        }
    }

    private static void configureLogger() throws IOException {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.WARN);
        rootLogger.addAppender(new FileAppender(
                new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} [%p] %c - %m%n"), "bot.log"));
    }


}

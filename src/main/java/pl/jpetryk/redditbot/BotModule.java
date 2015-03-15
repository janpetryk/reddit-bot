package pl.jpetryk.redditbot;

import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import pl.jpetryk.redditbot.bot.impl.TweetsInCommentsBot;
import pl.jpetryk.redditbot.connectors.*;
import pl.jpetryk.redditbot.utils.CommentParser;
import pl.jpetryk.redditbot.utils.PropertiesReader;
import pl.jpetryk.redditbot.utils.ResponseCommentCreator;
import pl.jpetryk.redditbot.utils.ResponseCommentCreatorInterface;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jan on 22/02/15.
 */
public class BotModule extends AbstractModule {
    @Override
    protected void configure() {
        try {
            Names.bindProperties(binder(), new PropertiesReader("resources/twitter.properties").getProperties());
            bind(TwitterConnectorInterface.class).to(Twitter4JConnector.class);
            bind(CommentParser.class);

            Names.bindProperties(binder(), new PropertiesReader("resources/template/template.properties").getProperties());
            bindConstant().annotatedWith(Names.named("response-template")).to(
                    Files.toString(new File("resources/template/reply-template"), Charset.defaultCharset()));
            bindConstant().annotatedWith(Names.named("footer-template")).to(
                    Files.toString(new File("resources/template/footer-template"), Charset.defaultCharset()));

            bind(ResponseCommentCreatorInterface.class).to(ResponseCommentCreator.class);
            PropertiesReader botProperties = new PropertiesReader("resources/bot.properties");
            Names.bindProperties(binder(), botProperties.getProperties());
            bind(ImgurConnectorInterface.class).to(ImgurConnector.class);
            bind(RedditConnectorInterface.class).to(JrawRedditConnector.class);
            List<String> blacklist = new ArrayList<>(Arrays.asList(botProperties.getProperty("blacklist").split(", ")));
            blacklist.add(botProperties.getProperty("reddit-login"));
            bind(new TypeLiteral<List<String>>() {
            }).toInstance(blacklist);
            bind(TweetsInCommentsBot.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

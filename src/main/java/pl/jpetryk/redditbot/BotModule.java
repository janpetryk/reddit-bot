package pl.jpetryk.redditbot;

import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import pl.jpetryk.redditbot.bot.impl.TweetsInCommentsBot;
import pl.jpetryk.redditbot.connectors.*;
import pl.jpetryk.redditbot.parser.BaseCommentParser;
import pl.jpetryk.redditbot.parser.CommentParser;
import pl.jpetryk.redditbot.utils.PropertiesReader;
import pl.jpetryk.redditbot.utils.ResponseCommentCreator;
import pl.jpetryk.redditbot.utils.ResponseCommentCreatorInterface;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
            Names.bindProperties(binder(), new PropertiesReader("twitter.properties").getProperties());
            bind(TwitterConnectorInterface.class).to(Twitter4JConnector.class);
            bind(CommentParser.class).to(BaseCommentParser.class);

            Names.bindProperties(binder(), new PropertiesReader("template/template.properties").getProperties());
            bindConstant().annotatedWith(Names.named("response-template")).to(
                    CharStreams.toString(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("template/reply-template"))));
            bindConstant().annotatedWith(Names.named("footer-template")).to(
                    CharStreams.toString(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("template/footer-template"))));

            bind(ResponseCommentCreatorInterface.class).to(ResponseCommentCreator.class);
            PropertiesReader botProperties = new PropertiesReader("bot.properties");
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

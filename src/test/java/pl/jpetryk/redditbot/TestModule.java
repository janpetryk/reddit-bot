package pl.jpetryk.redditbot;

import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import pl.jpetryk.redditbot.bot.AbstractRedditBot;
import pl.jpetryk.redditbot.bot.impl.TweetsInCommentsBot;
import pl.jpetryk.redditbot.connectors.ImgurConnectorInterface;
import pl.jpetryk.redditbot.connectors.RedditConnectorInterface;
import pl.jpetryk.redditbot.connectors.TwitterConnectorInterface;
import pl.jpetryk.redditbot.mock.connectors.ImgurConnectorMock;
import pl.jpetryk.redditbot.mock.connectors.RedditConnectorMock;
import pl.jpetryk.redditbot.mock.connectors.TwitterConnectorMock;
import pl.jpetryk.redditbot.mock.parser.CommentParserMock;
import pl.jpetryk.redditbot.parser.BaseCommentParser;
import pl.jpetryk.redditbot.parser.CommentParser;
import pl.jpetryk.redditbot.utils.PropertiesReader;
import pl.jpetryk.redditbot.utils.ResponseCommentCreator;
import pl.jpetryk.redditbot.utils.ResponseCommentCreatorInterface;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan on 2015-12-30.
 */
public class TestModule extends AbstractModule {
    @Override
    protected void configure() {
        try {
            Names.bindProperties(binder(), new PropertiesReader("resources/twitter.properties").getProperties());
            Names.bindProperties(binder(), new PropertiesReader("resources/bot.properties").getProperties());
            Names.bindProperties(binder(), new PropertiesReader("resources/template/template.properties").getProperties());
            bindConstant().annotatedWith(Names.named("response-template")).to(
                    Files.toString(new File("resources/template/reply-template"), Charset.defaultCharset()));
            bindConstant().annotatedWith(Names.named("footer-template")).to(
                    Files.toString(new File("resources/template/footer-template"), Charset.defaultCharset()));
            bind(RedditConnectorInterface.class).to(RedditConnectorMock.class);
            bind(ImgurConnectorInterface.class).to(ImgurConnectorMock.class);
            bind(TwitterConnectorInterface.class).to(TwitterConnectorMock.class);
            bind(ResponseCommentCreatorInterface.class).to(ResponseCommentCreator.class);
            bind(CommentParser.class).to(CommentParserMock.class);
            bind(new TypeLiteral<List<String>>() {
            }).toInstance(new ArrayList<String>());
            bind(AbstractRedditBot.class).to(TweetsInCommentsBot.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package pl.jpetryk.redditbot;

import com.google.common.io.CharStreams;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import okhttp3.OkHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import pl.jpetryk.redditbot.bot.AbstractRedditBot;
import pl.jpetryk.redditbot.bot.impl.TweetsInCommentsBot;
import pl.jpetryk.redditbot.connectors.ImgurConnectorInterface;
import pl.jpetryk.redditbot.connectors.RedditConnectorInterface;
import pl.jpetryk.redditbot.connectors.TwitterConnectorInterface;
import pl.jpetryk.redditbot.mock.connectors.ImgurConnectorMock;
import pl.jpetryk.redditbot.mock.connectors.RedditConnectorMock;
import pl.jpetryk.redditbot.mock.connectors.TwitterConnectorMock;
import pl.jpetryk.redditbot.mock.parser.CommentParserMock;
import pl.jpetryk.redditbot.parser.CommentParser;
import pl.jpetryk.redditbot.utils.PropertiesReader;
import pl.jpetryk.redditbot.utils.ResponseCommentCreator;
import pl.jpetryk.redditbot.utils.ResponseCommentCreatorInterface;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan on 2015-12-30.
 */
public class TestModule extends AbstractModule {
    @Override
    protected void configure() {
        try {
            Names.bindProperties(binder(), new PropertiesReader("twitter.properties").getProperties());
            Names.bindProperties(binder(), new PropertiesReader("bot.properties").getProperties());
            Names.bindProperties(binder(), new PropertiesReader("template/template.properties").getProperties());
            bindConstant().annotatedWith(Names.named("response-template")).to(
                    CharStreams.toString(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("template/reply-template"))));
            bindConstant().annotatedWith(Names.named("footer-template")).to(
                    CharStreams.toString(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("template/footer-template"))));
            bind(RedditConnectorInterface.class).to(RedditConnectorMock.class);
            bind(ImgurConnectorInterface.class).to(ImgurConnectorMock.class);
            bind(TwitterConnectorInterface.class).to(TwitterConnectorMock.class);
            bind(ResponseCommentCreatorInterface.class).to(ResponseCommentCreator.class);
            bind(OkHttpClient.class).toInstance(BotModule.okHttpClient());
            bind(ObjectMapper.class).toInstance(BotModule.objectMapper());
            bind(CommentParser.class).to(CommentParserMock.class);
            bind(new TypeLiteral<List<String>>() {
            }).toInstance(new ArrayList<String>());
            bind(AbstractRedditBot.class).to(TweetsInCommentsBot.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

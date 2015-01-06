package pl.jpetryk.redditbot;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import pl.jpetryk.redditbot.model.AuthorizationType;
import pl.jpetryk.redditbot.model.Configuration;
import pl.jpetryk.redditbot.utils.PropertiesReader;

/**
 * Created by Jan on 06/01/15.
 */
public class Runner {

    public static void main(String[] args) throws InterruptedException {
        {
            Logger rootLogger = Logger.getRootLogger();
            rootLogger.setLevel(Level.INFO);
            rootLogger.addAppender(new ConsoleAppender(
                    new PatternLayout("%-6r [%p] %c - %m%n")));
        }
        PropertiesReader propertiesReader = new PropertiesReader("testbot.properties");
        Configuration configuration = new Configuration.Builder()
                .addSubreddit("test")
                .authorizationType(AuthorizationType.OAUTH)
                .login(propertiesReader.getProperty("reddit-login"))
                .password(propertiesReader.getProperty("reddit-password"))
                .clientId(propertiesReader.getProperty("reddit-client-id"))
                .clientSecret(propertiesReader.getProperty("reddit-client-secret"))
                .userAgent(propertiesReader.getProperty("reddit-useragent"))
                .build();
        HelloRedditBot bot = new HelloRedditBot(configuration);

        while(true){
            bot.run();
            Thread.sleep(5000);
        }
    }
}

package pl.jpetryk.redditbot.model;

import com.google.common.collect.ImmutableList;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jan on 05/01/15.
 */
public class Configuration {

    private String userAgent;
    private String login;
    private String clientId;
    private String clientSecret;
    private String password;
    private List<String> subredditList;
    private AuthorizationType authorizationType;


    public Configuration(Builder builder) {
        userAgent = builder.userAgent;
        login = builder.login;
        clientId = builder.clientId;
        clientSecret = builder.clientSecret;
        password = builder.password;
        subredditList = ImmutableList.copyOf(builder.subredditList);
        authorizationType = builder.authorizationType;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getSubredditList() {
        return subredditList;
    }

    public String getAbsoluteSubredditPath() {
        Iterator<String> iterator = subredditList.iterator();
        String absolutePath = iterator.next();
        while (iterator.hasNext()) {
            absolutePath = "+" + iterator.next();
        }
        return absolutePath;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public AuthorizationType getAuthorizationType() {
        return authorizationType;
    }

    public static class Builder {

        private static final String FIELD_IS_EMPTY_MESSAGE = "Mandatory field %s is not set.";
        private static final String OAUTH_MANDATORY_FIELD_IS_EMPTY_MESSAGE = "OAuth mandatory field %s is not set.";

        private String userAgent;
        private String login;
        private String clientId;
        private String clientSecret;
        private String password;
        private List<String> subredditList;
        private AuthorizationType authorizationType;

        public Builder() {
            subredditList = new ArrayList<>();
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder login(String login) {
            this.login = login;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder addSubredditList(List<String> subredditList) {
            this.subredditList.addAll(subredditList);
            return this;
        }

        public Builder addSubreddit(String subreddit) {
            subredditList.add(subreddit);
            return this;
        }

        public Builder authorizationType(AuthorizationType authorizationType) {
            this.authorizationType = authorizationType;
            return this;
        }

        public Configuration build() {
            validate();
            return new Configuration(this);
        }

        private void validate() {
            if (userAgent == null) {
                throw new InvalidStateException(String.format(FIELD_IS_EMPTY_MESSAGE, "userAgent"));
            }
            if (login == null) {
                throw new InvalidStateException(String.format(FIELD_IS_EMPTY_MESSAGE, "login"));
            }
            if (password == null) {
                throw new InvalidStateException(String.format(FIELD_IS_EMPTY_MESSAGE, "password"));
            }
            if (subredditList.isEmpty()) {
                throw new InvalidStateException(String.format(FIELD_IS_EMPTY_MESSAGE, "subredditList"));
            }
            if (authorizationType == null) {
                throw new InvalidStateException(String.format(FIELD_IS_EMPTY_MESSAGE, "authorizationType"));
            }
            if (authorizationType == AuthorizationType.OAUTH && clientId == null) {
                throw new InvalidStateException(String.format(OAUTH_MANDATORY_FIELD_IS_EMPTY_MESSAGE, "clientId"));
            }
            if (authorizationType == AuthorizationType.OAUTH && clientSecret == null) {
                throw new InvalidStateException(String.format(OAUTH_MANDATORY_FIELD_IS_EMPTY_MESSAGE, "clientSecret"));
            }
        }
    }
}

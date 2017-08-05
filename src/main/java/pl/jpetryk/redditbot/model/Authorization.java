package pl.jpetryk.redditbot.model;

public class Authorization {

    private String accessToken;
    private String tokenType;
    private String expiresIn;
    private String scope;

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public String getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return "Authorization{" +
                "accessToken='" + accessToken + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn='" + expiresIn + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}

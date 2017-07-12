package me.savvy.rixa.modules.twitter;

import me.savvy.rixa.guild.RixaGuild;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by savit on 7/9/2017.
 */
public class TwitterModule {

    private final TwitterStream twitterStream;
    private ConfigurationBuilder configurationBuilder;
    private Twitter twitter;
    private TwitterFactory twitterFactory;
    private final RixaGuild rixaGuild;
    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;

    public TwitterModule(RixaGuild rixaGuild, String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
        this.rixaGuild = rixaGuild;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
        configurationBuilder = new ConfigurationBuilder()
                .setOAuthConsumerKey(getConsumerKey())
                .setOAuthConsumerSecret(getConsumerSecret())
                .setOAuthAccessToken(getAccessToken())
                .setOAuthAccessTokenSecret(getAccessTokenSecret());
        twitterFactory = new TwitterFactory(configurationBuilder.build());
        twitterStream = new TwitterStreamFactory().getInstance();
        twitter = twitterFactory.getInstance();
    }

    public ConfigurationBuilder getConfigurationBuilder() {
        return configurationBuilder;
    }

    public TwitterFactory getTwitterFactory() {
        return twitterFactory;
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    public void setAccessTokenSecret(String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
    }

    public TwitterStream getTwitterStream() {
        return twitterStream;
    }

    public RixaGuild getRixaGuild() {
        return rixaGuild;
    }
}

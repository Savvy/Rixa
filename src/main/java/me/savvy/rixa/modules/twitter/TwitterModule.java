package me.savvy.rixa.modules.twitter;

import lombok.Getter;
import lombok.Setter;
import me.savvy.rixa.guild.RixaGuild;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by savit on 7/9/2017.
 */
public class TwitterModule {
    
    @Getter
    private final TwitterStream twitterStream;
    @Getter
    private ConfigurationBuilder configurationBuilder;
    @Getter
    private Twitter twitter;
    @Getter
    private TwitterFactory twitterFactory;
    @Getter
    private final RixaGuild rixaGuild;
    @Getter @Setter
    private String consumerKey;
    @Getter @Setter
    private String consumerSecret;
    @Getter @Setter
    private String accessToken;
    @Getter @Setter
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
    
}

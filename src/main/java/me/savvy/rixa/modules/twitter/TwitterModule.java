package me.savvy.rixa.modules.twitter;

import lombok.Getter;
import lombok.Setter;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.modules.RixaModule;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by savit on 7/9/2017.
 */
public class TwitterModule implements RixaModule {

    @Getter private boolean enabled;
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
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);
        twitterFactory = new TwitterFactory(configurationBuilder.build());
        twitterStream = new TwitterStreamFactory().getInstance();
        twitter = twitterFactory.getInstance();
    }

    @Override
    public String getName() {
        return "Twitter";
    }

    @Override
    public String getDescription() {
        return "Twitter feed, tweet & more.";
    }

    @Override
    public void load() { }

    @Override
    public void save() { }
}
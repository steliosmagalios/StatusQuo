package gr.uom.socialmediaaggregator.data;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterWrapper {

    private static Twitter instance;

    private TwitterWrapper() {}

    public static Twitter getInstance() {
        if (instance == null) {
            ConfigurationBuilder builder = new ConfigurationBuilder()
                    .setOAuthConsumerKey("")
                    .setOAuthConsumerSecret("")
                    .setOAuthAccessToken("")
                    .setOAuthAccessTokenSecret("");
            instance = new TwitterFactory(builder.build()).getInstance();
        }
        return instance;
    }
}

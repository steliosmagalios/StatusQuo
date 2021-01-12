package gr.uom.socialmediaaggregator.data;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterWrapper {

    public static final String TAG = "SMA";
    private static String API_KEY;
    private static String API_SECRET;

    private static String ACCESS_TOKEN;
    private static String ACCESS_SECRET;


    private static Twitter instance;

    private TwitterWrapper() {}

    public static Twitter getInstance() {
        if (instance == null) {
            ConfigurationBuilder builder = new ConfigurationBuilder()
                    .setOAuthConsumerKey(API_KEY)
                    .setOAuthConsumerSecret(API_SECRET)
                    .setOAuthAccessToken(ACCESS_TOKEN)
                    .setOAuthAccessTokenSecret(ACCESS_SECRET);
            instance = new TwitterFactory(builder.build()).getInstance();
        }
        return instance;
    }

    public static void setApiKeys(String apiKey, String apiSecret) {
        API_KEY = apiKey;
        API_SECRET = apiSecret;
    }

    public static void setUserKeys(String accessToken, String accessSecret) {
        ACCESS_TOKEN = accessToken;
        ACCESS_SECRET = accessSecret;
    }
}

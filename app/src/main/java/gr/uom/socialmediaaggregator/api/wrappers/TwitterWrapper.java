package gr.uom.socialmediaaggregator.api.wrappers;

import java.util.ArrayList;
import java.util.List;

import gr.uom.socialmediaaggregator.api.parsers.TwitterParser;
import gr.uom.socialmediaaggregator.api.tasks.GetTwitterBearerToken;
import gr.uom.socialmediaaggregator.data.model.Post;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Trend;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterWrapper {

    // TODO: 25-Jan-21 Actually WRAP the twitter API

    private static String API_KEY;
    private static String API_SECRET;

    private static String ACCESS_TOKEN;
    private static String ACCESS_SECRET;

    private static Twitter tInstance;

    private TwitterWrapper() {}

    public static Twitter getOldInstance() {
        if (tInstance == null) {
            ConfigurationBuilder builder = new ConfigurationBuilder()
                    .setOAuthConsumerKey(API_KEY)
                    .setOAuthConsumerSecret(API_SECRET)
                    .setApplicationOnlyAuthEnabled(true);
            tInstance = new TwitterFactory(builder.build()).getInstance();
            new GetTwitterBearerToken().execute();
        }
        return tInstance;
    }

    public static void setApiKeys(String apiKey, String apiSecret) {
        API_KEY = apiKey;
        API_SECRET = apiSecret;
    }

    public static void setUserKeys(String accessToken, String accessSecret) {
        ACCESS_TOKEN = accessToken;
        ACCESS_SECRET = accessSecret;
    }



    private static TwitterWrapper instance = new TwitterWrapper();

    public static TwitterWrapper init() {
        return instance;
    }

    public static TwitterWrapper getInstance() {
        return instance;
    }

    public List<Trend> fetchTrendsForPlace(int woeid) throws TwitterException {
        Twitter twitter = TwitterFactory.getSingleton();
        ArrayList<Trend> trendsList = new ArrayList<>();

        // TODO: 27-Jan-21 Fix
//        Trends trends = twitter.getPlaceTrends(woeid);
//        trendsList.addAll(Arrays.asList(trends.getTrends()));

        return trendsList;
    }

    public List<Post> fetchPostsWithTrend(Trend trend) throws TwitterException {
        Twitter twitter = TwitterFactory.getSingleton();
        QueryResult result = twitter.search(new Query(trend.getQuery()));
        return new ArrayList<>(TwitterParser.getInstance().parse(result.getTweets()));
    }

}

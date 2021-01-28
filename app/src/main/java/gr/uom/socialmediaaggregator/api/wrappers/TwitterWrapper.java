package gr.uom.socialmediaaggregator.api.wrappers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gr.uom.socialmediaaggregator.BuildConfig;
import gr.uom.socialmediaaggregator.api.parsers.TwitterParser;
import gr.uom.socialmediaaggregator.data.model.Post;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.StatusUpdate;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterWrapper {

    private static TwitterWrapper instance = new TwitterWrapper();

    private Twitter twitter;

    private TwitterWrapper() {
    }

    public static TwitterWrapper init(String accessToken, String accessTokenSecret, boolean useOAuth2) {
        instance = new TwitterWrapper();
        ConfigurationBuilder config = new ConfigurationBuilder()
                .setApplicationOnlyAuthEnabled(useOAuth2)
                .setOAuthConsumerKey(BuildConfig.TWITTER_API_KEY)
                .setOAuthConsumerSecret(BuildConfig.TWITTER_API_KEY_SECRET)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);
        instance.twitter = new TwitterFactory(config.build()).getInstance();
        return instance;
    }

    public static TwitterWrapper init() {
        instance = new TwitterWrapper();
        ConfigurationBuilder config = new ConfigurationBuilder()
                .setApplicationOnlyAuthEnabled(true)
                .setOAuthConsumerKey(BuildConfig.TWITTER_API_KEY)
                .setOAuthConsumerSecret(BuildConfig.TWITTER_API_KEY_SECRET);
        instance.twitter = new TwitterFactory(config.build()).getInstance();
        return instance;
    }

    public static TwitterWrapper getInstance() {
        return instance;
    }

    public List<Trend> fetchTrendsForPlace(int woeid) throws TwitterException {
        Trends trends = twitter.getPlaceTrends(woeid);
        return new ArrayList<>(Arrays.asList(trends.getTrends()));
    }

    public List<Post> fetchPostsWithTrend(Trend trend) throws TwitterException {
        QueryResult result = twitter.search(new Query(trend.getQuery()));
        return new ArrayList<>(TwitterParser.getInstance().parse(result.getTweets()));
    }

    public void publishTweet(String message, String imageUrl) throws TwitterException, IOException {
        StatusUpdate statusUpdate = new StatusUpdate(message);
        if (imageUrl != null)
            statusUpdate.setMedia("img", new URL(imageUrl).openConnection().getInputStream());
        twitter.updateStatus(statusUpdate);
    }

    public OAuth2Token requestOAuth2Token() throws TwitterException, IllegalStateException {
        return twitter.getOAuth2Token();
    }

    public void setOAuth2Token(OAuth2Token token) {
        if (token != null)
            twitter.setOAuth2Token(token);
    }

}

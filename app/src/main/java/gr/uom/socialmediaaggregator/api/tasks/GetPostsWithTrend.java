package gr.uom.socialmediaaggregator.api.tasks;

import android.net.Uri;
import android.os.AsyncTask;

import com.facebook.AccessToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import gr.uom.socialmediaaggregator.api.wrappers.InstagramWrapper;
import gr.uom.socialmediaaggregator.api.wrappers.TwitterWrapper;
import gr.uom.socialmediaaggregator.data.SocialMediaPlatform;
import gr.uom.socialmediaaggregator.data.model.Post;
import gr.uom.socialmediaaggregator.ui.main.ui.view_posts.PostsAdapter;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Trend;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class GetPostsWithTrend extends AsyncTask<Void, Void, List<Post>> {

    private static final String TAG = "SMA";

    private static OkHttpClient CLIENT = new OkHttpClient();

    static {
        CLIENT.setProtocols(Arrays.asList(Protocol.HTTP_1_1));
    }

    private Trend trend;
    private PostsAdapter adapter;

    public GetPostsWithTrend(Trend trend, PostsAdapter adapter) {
        this.trend = trend;
        this.adapter = adapter;
    }

    @Override
    protected List<Post> doInBackground(Void... voids) {
        List<Post> posts = new ArrayList<>();

        Twitter twitter = TwitterWrapper.getInstance();

        // Twitter
        try {
            QueryResult results = twitter.search(new Query(trend.getQuery()));
            posts.addAll(parseTwitterPosts(results.getTweets()));
        } catch (TwitterException e) {
            e.printStackTrace();
            return null;
        }

        // Instagram
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if (isLoggedIn) {
            InstagramWrapper igWrapper = new InstagramWrapper(accessToken);
            try {
                JSONArray data = igWrapper.fetchPostsWithTrend(trend);
                posts.addAll(parseInstagramPosts(data));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return posts;
    }

    @Override
    protected void onPostExecute(List<Post> posts) {
        Collections.sort(posts);
        Collections.reverse(posts); // Sort the post from newer to older
        adapter.setPostList(posts);
    }

    private List<Post> parseInstagramPosts(JSONArray data) {
        ArrayList<Post> posts = new ArrayList<>();

        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject item = data.getJSONObject(i);

                Post.Builder builder = new Post.Builder()
                    .setSocialMediaPlatform(SocialMediaPlatform.Instagram)
                    .setBody(item.getString("caption"))
                    // Application needs to exit development mode to get user details
                    .setUser("unknown_user")
                    .setRedirectUri(Uri.parse(item.getString("permalink")))
                    .setPostedDate(DateTime.parse(item.getString("timestamp")).toDate());
                posts.add(builder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return posts;
    }

    private List<Post> parseTwitterPosts(List<twitter4j.Status> tweets) {
        List<Post> posts = new ArrayList<>();
        tweets.forEach(status -> {
            Post.Builder builder = new Post.Builder()
                .setSocialMediaPlatform(SocialMediaPlatform.Twitter)
                .setUser(status.getUser().getScreenName())
                .setBody(status.getText())
                .setPostedDate(status.getCreatedAt())
                .setRedirectUri(Uri.parse("http://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId()));
            posts.add(builder.build());
        });

        return posts;
    }
}

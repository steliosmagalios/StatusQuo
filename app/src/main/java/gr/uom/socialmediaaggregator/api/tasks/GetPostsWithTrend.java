package gr.uom.socialmediaaggregator.api.tasks;

import android.net.Uri;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import gr.uom.socialmediaaggregator.data.TwitterWrapper;
import gr.uom.socialmediaaggregator.data.model.post.Platform;
import gr.uom.socialmediaaggregator.data.model.post.Post;
import gr.uom.socialmediaaggregator.ui.main.ui.view_posts.PostsAdapter;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Trend;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class GetPostsWithTrend extends AsyncTask<Void, Void, List<Status>> {

    private Trend trend;
    private PostsAdapter adapter;

    public GetPostsWithTrend(Trend trend, PostsAdapter adapter) {
        this.trend = trend;
        this.adapter = adapter;
    }

    @Override
    protected List<twitter4j.Status> doInBackground(Void... voids) {
        Twitter twitter = TwitterWrapper.getInstance();

        try {
            QueryResult results = twitter.search(new Query(trend.getQuery()));
            return results.getTweets();
        } catch (TwitterException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<twitter4j.Status> statuses) {
        if (statuses != null) {
            List<Post> tweets = parseTwitterPosts(statuses);

            adapter.setPostList(tweets);
        }
    }

    private List<Post> parseTwitterPosts(List<twitter4j.Status> tweets) {
        List<Post> posts = new ArrayList<>();
        tweets.forEach(status -> {
            Post.Builder builder = new Post.Builder()
                .setPlatform(Platform.Twitter)
                .setUser(status.getUser().getScreenName())
                .setBody(status.getText())
                .setPostedDate(status.getCreatedAt())
                .setRedirectUri(Uri.parse("http://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId()));
            posts.add(builder.build());
        });

        return posts;
    }
}

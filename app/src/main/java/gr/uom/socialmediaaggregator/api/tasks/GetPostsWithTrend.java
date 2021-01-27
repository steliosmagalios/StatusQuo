package gr.uom.socialmediaaggregator.api.tasks;

import android.os.AsyncTask;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gr.uom.socialmediaaggregator.api.comparators.IPostComparator;
import gr.uom.socialmediaaggregator.api.comparators.NewestToOldestPostComparator;
import gr.uom.socialmediaaggregator.api.parsers.InstagramParser;
import gr.uom.socialmediaaggregator.api.wrappers.InstagramWrapper;
import gr.uom.socialmediaaggregator.api.wrappers.TwitterWrapper;
import gr.uom.socialmediaaggregator.data.model.Post;
import gr.uom.socialmediaaggregator.ui.main.ui.view_posts.PostsAdapter;
import twitter4j.Trend;

public class GetPostsWithTrend extends AsyncTask<Void, Void, List<Post>> {

    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final IPostComparator COMPARATOR = new NewestToOldestPostComparator();

    static {
        CLIENT.setProtocols(Collections.singletonList(Protocol.HTTP_1_1));
    }

    private final Trend trend;
    private final PostsAdapter adapter;

    public GetPostsWithTrend(Trend trend, PostsAdapter adapter) {
        this.trend = trend;
        this.adapter = adapter;
    }

    @Override
    protected List<Post> doInBackground(Void... voids) {
        List<Post> posts = new ArrayList<>();

        try {
            // Twitter
            posts.addAll(TwitterWrapper.getInstance().fetchPostsWithTrend(trend));

            // Instagram
            JSONArray data = InstagramWrapper.getInstance().fetchPostsWithTrend(trend);
            posts.addAll(InstagramParser.getInstance().parse(data));

            // IMPORTANT: Fetching posts from Facebook is NOT supported
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return posts;
    }

    @Override
    protected void onPostExecute(List<Post> posts) {
        // Sort the list from newest to oldest and display it
        posts.sort(COMPARATOR);
        adapter.setPostList(posts);
    }

}

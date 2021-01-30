package gr.uom.socialmediaaggregator.api.parsers;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import gr.uom.socialmediaaggregator.data.Platform;
import gr.uom.socialmediaaggregator.data.model.Post;
import twitter4j.Status;

public class TwitterParser implements IParser<List<Status>> {

    private static final String TWITTER_STATUS_URI_TEMPLATE = "https://twitter.com/%s/status/%d";

    private static TwitterParser instance;

    private TwitterParser() {}

    public static TwitterParser getInstance() {
        if (instance == null)
            instance = new TwitterParser();
        return instance;
    }

    @Override
    public List<Post> parse(List<Status> data) {
        ArrayList<Post> posts = new ArrayList<>();
        data.forEach(status -> {
            Post.Builder builder = new Post.Builder()
                    .setPlatform(Platform.Twitter)
                    .setUser(status.getUser().getScreenName())
                    .setBody(status.getText())
                    .setPostedDate(status.getCreatedAt())
                    .setRedirectUri(Uri.parse(String.format(Locale.getDefault(), TWITTER_STATUS_URI_TEMPLATE, status.getUser().getScreenName(), status.getId())));
            posts.add(builder.build());
        });
        return posts;
    }

}

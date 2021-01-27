package gr.uom.socialmediaaggregator.api.parsers;

import android.net.Uri;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gr.uom.socialmediaaggregator.data.SocialMediaPlatform;
import gr.uom.socialmediaaggregator.data.model.Post;

public class InstagramParser implements IParser<JSONArray> {


    private static InstagramParser instance;

    private InstagramParser() {}

    public static InstagramParser getInstance() {
        if (instance == null)
            instance = new InstagramParser();
        return instance;
    }

    @Override
    public List<Post> parse(JSONArray data) {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return posts;
    }
}

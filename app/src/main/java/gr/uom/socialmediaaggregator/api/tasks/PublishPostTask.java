package gr.uom.socialmediaaggregator.api.tasks;

import android.net.Uri;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import gr.uom.socialmediaaggregator.api.wrappers.FacebookWrapper;
import gr.uom.socialmediaaggregator.api.wrappers.TwitterWrapper;
import gr.uom.socialmediaaggregator.data.SocialMediaPlatform;

public class PublishPostTask extends AsyncTask<Void, Void, Void> {

    private final String message;
    private final Uri imageUri;
    private final List<SocialMediaPlatform> platforms;

    private final List<Consumer<Void>> callbackList = new ArrayList<>();

    public PublishPostTask(String message, Uri imageUri, List<SocialMediaPlatform> platforms) {
        this.message = message;
        this.imageUri = imageUri;
        this.platforms = platforms;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        platforms.forEach(platform -> {
            try {
                switch (platform) {
                    case Twitter:
                        TwitterWrapper.getInstance().publishTweet(
                                message,
                                imageUri != null ? imageUri.toString() : null
                        );
                        break;
                    case Facebook:
                        FacebookWrapper.getInstance().publishPostToFacebookPage(
                                message,
                                imageUri != null ? imageUri.toString() : null
                        );
                        break;
                    case Instagram:
                        FacebookWrapper.getInstance().publishPostToInstagram(
                                message,
                                imageUri != null ? imageUri.toString() : null
                        );
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        callbackList.forEach(callback -> {
            callback.accept(v);
        });
    }

    public void addCallback(Consumer<Void> callback) {
        callbackList.add(callback);
    }
}

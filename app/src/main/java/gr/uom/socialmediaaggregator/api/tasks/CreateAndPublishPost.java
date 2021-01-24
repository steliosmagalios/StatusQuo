package gr.uom.socialmediaaggregator.api.tasks;

import android.os.AsyncTask;

import java.io.InputStream;
import java.util.List;

import gr.uom.socialmediaaggregator.api.wrappers.TwitterWrapper;
import gr.uom.socialmediaaggregator.data.SocialMediaPlatform;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class CreateAndPublishPost extends AsyncTask<Void, Void, Void> {

    private String body;
    private InputStream img;
    private List<SocialMediaPlatform> platformsToPublish;

    public CreateAndPublishPost(String body, InputStream img, List<SocialMediaPlatform> platformsToPublish) {
        this.body = body;
        this.img = img;
        this.platformsToPublish = platformsToPublish;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            publishOnTwitter();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // FB and IG
        return null;
    }

    private void publishOnTwitter() throws TwitterException {
        Twitter twitter = TwitterWrapper.getInstance();
        StatusUpdate status = new StatusUpdate(body);
        if (img != null)
            status.setMedia("img", img);

        twitter.updateStatus(status);
    }

}

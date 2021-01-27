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

    private final String body;
    private final InputStream img;
    private final List<SocialMediaPlatform> platforms;

    public CreateAndPublishPost(String body, InputStream img, List<SocialMediaPlatform> platforms) {
        this.body = body;
        this.img = img;
        this.platforms = platforms;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        platforms.forEach(platform -> {
            try {
                switch (platform) {
                    case Twitter:
                        publishOnTwitter();
                        break;
                    case Facebook:
                        publishOnFacebook();
                        break;
                    case Instagram:
                        publishOnInstagram();
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        return null;
    }

    private void publishOnTwitter() throws TwitterException {
        Twitter twitter = TwitterWrapper.getOldInstance();
        StatusUpdate status = new StatusUpdate(body);
        if (img != null)
            status.setMedia("img", img);
        twitter.updateStatus(status);
    }

    private void publishOnFacebook() {
        // TODO: 27-Jan-21 Implement
    }

    private void publishOnInstagram() {
        // TODO: 27-Jan-21 Implement
    }


}

package gr.uom.socialmediaaggregator.api.tasks;

import android.os.AsyncTask;

import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;

public class GetTwitterBearerToken extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            OAuth2Token bearerToken = TwitterFactory.getSingleton().getOAuth2Token();
            TwitterFactory.getSingleton().setOAuth2Token(bearerToken);
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        return null;
    }
}

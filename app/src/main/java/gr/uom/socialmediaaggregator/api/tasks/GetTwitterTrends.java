package gr.uom.socialmediaaggregator.api.tasks;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import java.util.Arrays;
import java.util.List;

import gr.uom.socialmediaaggregator.api.wrappers.TwitterWrapper;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class GetTwitterTrends extends AsyncTask<Void, Void, Trends> {

    private static final int GREECE_WOEID = 23424833;

    private final MutableLiveData<List<Trend>> mutableTrends;

    public GetTwitterTrends(MutableLiveData<List<Trend>> mutableTrends) {
        this.mutableTrends = mutableTrends;
    }

    @Override
    protected Trends doInBackground(Void... voids) {
        Twitter twitter = TwitterWrapper.getInstance();

        // TODO: 15-Jan-21 Use Twitter API to fetch the trends if the user is not signed

        Trends trends;
        try {
             trends = twitter.getPlaceTrends(GREECE_WOEID);
        } catch (TwitterException e) {
            trends = null;
            e.printStackTrace();
        }

        return trends;
    }

    @Override
    protected void onPostExecute(Trends trends) {
        mutableTrends.setValue(Arrays.asList(trends.getTrends()));
    }
}

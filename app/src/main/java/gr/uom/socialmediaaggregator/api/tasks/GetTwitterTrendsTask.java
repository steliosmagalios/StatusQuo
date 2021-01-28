package gr.uom.socialmediaaggregator.api.tasks;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import gr.uom.socialmediaaggregator.api.wrappers.TwitterWrapper;
import twitter4j.Trend;
import twitter4j.TwitterException;

public class GetTwitterTrendsTask extends AsyncTask<Void, Void, List<Trend>> {

    private static final int GREECE_WOEID = 23424833;

    private final MutableLiveData<List<Trend>> mutableTrends;

    public GetTwitterTrendsTask(MutableLiveData<List<Trend>> mutableTrends) {
        this.mutableTrends = mutableTrends;
    }

    @Override
    protected List<Trend> doInBackground(Void... voids) {
        try {
            return TwitterWrapper.getInstance().fetchTrendsForPlace(GREECE_WOEID);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Trend> trends) {
        mutableTrends.setValue(trends);
    }
}

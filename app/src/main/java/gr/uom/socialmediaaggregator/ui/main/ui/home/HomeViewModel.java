package gr.uom.socialmediaaggregator.ui.main.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import gr.uom.socialmediaaggregator.api.tasks.GetTwitterTrends;
import twitter4j.Trend;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<Trend>> trends;

    public HomeViewModel() {
    }

    public LiveData<List<Trend>> getTrends() {
        if (trends == null) {
            trends = new MutableLiveData<>();
            fetchTrends();
        }
        return trends;
    }

    private void fetchTrends() {
        // Fetch trends from TwitterAPI
        new GetTwitterTrends(trends).execute();
    }

    public List<Trend> searchOnTrends(String query) {
        List<Trend> trendsList = new ArrayList<>(trends.getValue());
        trendsList.removeIf(trend -> !trend.getName().contains(query));
        return trendsList;
    }
}
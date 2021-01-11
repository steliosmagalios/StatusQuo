package gr.uom.socialmediaaggregator.ui.main.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<String>> trends;

    public HomeViewModel() {
    }

    public LiveData<List<String>> getTrends() {
        if (trends == null) {
            trends = new MutableLiveData<>();
            fetchTrends();
        }
        return trends;
    }

    private void fetchTrends() {
        List<String> items = new ArrayList<>();

        // Fetch trends from TwitterAPI


        trends.setValue(items);
    }
}
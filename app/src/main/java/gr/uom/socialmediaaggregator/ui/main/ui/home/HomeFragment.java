package gr.uom.socialmediaaggregator.ui.main.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import gr.uom.socialmediaaggregator.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        ListView trendsList = root.findViewById(R.id.trendsList);

        TrendsAdapter adapter = new TrendsAdapter(root.getContext(), R.layout.trend_item, new ArrayList<>(), trendsList, this);
        trendsList.setAdapter(adapter);

        homeViewModel.getTrends().observe(getViewLifecycleOwner(), adapter::setTrendsList);

        return root;
    }
}
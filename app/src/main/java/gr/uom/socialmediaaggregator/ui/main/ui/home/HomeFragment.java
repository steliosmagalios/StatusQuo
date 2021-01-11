package gr.uom.socialmediaaggregator.ui.main.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import gr.uom.socialmediaaggregator.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        final RecyclerView trendsList = root.findViewById(R.id.trendsList);
        trendsList.setLayoutManager(layoutManager);

        TrendsAdapter adapter = new TrendsAdapter(new ArrayList<>());
        trendsList.setAdapter(adapter);

        homeViewModel.getTrends().observe(getViewLifecycleOwner(), adapter::setTrendsList);

        return root;
    }
}
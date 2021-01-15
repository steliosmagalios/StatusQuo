package gr.uom.socialmediaaggregator.ui.main.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import gr.uom.socialmediaaggregator.R;
import twitter4j.Trend;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private ListView trendsList;
    private EditText txtSearchText;
    private ImageButton btnSearch;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        trendsList = root.findViewById(R.id.trendsList);
        txtSearchText = root.findViewById(R.id.txtSearchText);
        btnSearch = root.findViewById(R.id.btnSearch);

        TrendsAdapter adapter = new TrendsAdapter(root.getContext(), R.layout.trend_item, new ArrayList<>(), trendsList, this);
        trendsList.setAdapter(adapter);

        homeViewModel.getTrends().observe(getViewLifecycleOwner(), adapter::setTrendsList);

        btnSearch.setOnClickListener(v -> {
            String queryText = txtSearchText.getText().toString();
            if (!queryText.isEmpty()) {
                List<Trend> filteredTrends = homeViewModel.searchOnTrends(queryText);
                adapter.setTrendsList(filteredTrends);
            } else {
                adapter.setTrendsList(homeViewModel.getTrends().getValue());
            }
        });

        return root;
    }
}
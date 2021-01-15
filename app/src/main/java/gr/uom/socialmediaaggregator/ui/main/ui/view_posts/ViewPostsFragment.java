package gr.uom.socialmediaaggregator.ui.main.ui.view_posts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import gr.uom.socialmediaaggregator.R;
import gr.uom.socialmediaaggregator.api.tasks.GetPostsWithTrend;
import gr.uom.socialmediaaggregator.data.model.post.Post;
import twitter4j.Trend;

public class ViewPostsFragment extends Fragment {

    private ListView postList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_posts, container, false);

        this.postList = view.findViewById(R.id.postList);

        // TODO: 15-Jan-21 Tidy up

        List<Post> posts = new ArrayList<>();
        PostsAdapter adapter = new PostsAdapter(view.getContext(), R.layout.post_item, posts, this.postList, this);
        this.postList.setAdapter(adapter);

        Trend trend = (Trend) getArguments().get("TREND");

        Log.d("SMA", trend.getQuery());

        new GetPostsWithTrend(trend, adapter).execute();

        return view;
    }
}
package gr.uom.socialmediaaggregator.ui.main.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.List;

import gr.uom.socialmediaaggregator.R;
import twitter4j.Trend;

public class TrendsAdapter extends ArrayAdapter<Trend> implements AdapterView.OnItemClickListener {

    private final LayoutInflater inflater;
    private final int layoutResource;
    private List<Trend> trendsList;

    private final ListView view;
    private final Fragment fragment;

    public TrendsAdapter(@NonNull Context context, int resource, @NonNull List<Trend> objects, ListView view, Fragment fragment) {
        super(context, resource, objects);
        this.inflater = LayoutInflater.from(context);
        this.layoutResource = resource;
        this.trendsList = objects;
        this.view = view;
        this.fragment = fragment;

        view.setOnItemClickListener(this);
    }

    @Override
    public int getCount() {
        return trendsList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = inflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Trend trend = trendsList.get(position);

        viewHolder.txtTrend.setText(trend.getName());

        return convertView;
    }

    public void setTrendsList(List<Trend> trendsList) {
        this.trendsList = trendsList;
        view.setAdapter(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Trend trend = trendsList.get(position);

        Bundle args = new Bundle();
        args.putSerializable("TREND", trend);

        NavHostFragment.findNavController(fragment).navigate(R.id.action_nav_home_to_viewPostsFragment, args);
    }

    public class ViewHolder {

        final TextView txtTrend;

        public ViewHolder(@NonNull View itemView) {
            txtTrend = itemView.findViewById(R.id.txtTrend);
        }
    }

}

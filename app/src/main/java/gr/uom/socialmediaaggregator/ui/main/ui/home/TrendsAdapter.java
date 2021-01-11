package gr.uom.socialmediaaggregator.ui.main.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import gr.uom.socialmediaaggregator.R;

public class TrendsAdapter extends RecyclerView.Adapter<TrendsAdapter.ViewHolder> {

    private List<String> trendsList;

    public TrendsAdapter(List<String> trendsList) {
        this.trendsList = trendsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trend_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        holder.txtTrend.setText(trendsList.get(position));
    }

    @Override
    public int getItemCount() {
        return trendsList.size();
    }

    public void setTrendsList(List<String> trendsList) {
        this.trendsList.clear();
        this.trendsList.addAll(trendsList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView txtTrend;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTrend = itemView.findViewById(R.id.txtTrend);
        }
    }

}

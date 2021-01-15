package gr.uom.socialmediaaggregator.ui.main.ui.view_posts;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import gr.uom.socialmediaaggregator.R;
import gr.uom.socialmediaaggregator.data.model.post.Post;

public class PostsAdapter extends ArrayAdapter<Post> implements AdapterView.OnItemClickListener {

    private final LayoutInflater inflater;
    private final int layoutResource;

    private List<Post> postList;
    private final ListView view;
    private final Fragment fragment;

    public PostsAdapter(@NonNull Context context, int resource, @NonNull List<Post> objects, ListView view, Fragment fragment) {
        super(context, resource, objects);
        inflater = LayoutInflater.from(context);
        layoutResource = resource;
        postList = objects;
        this.view = view;
        this.fragment = fragment;

        this.view.setOnItemClickListener(this);
    }

    @Override
    public int getCount() {
        return postList.size();
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

        Post post = postList.get(position);

        viewHolder.txtUser.setText(post.getUser());
        viewHolder.txtBody.setText(post.getTruncatedBody(50));
        viewHolder.txtDate.setText(post.getPostedDate().toString());


        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Post selectedPost = postList.get(position);

        Intent intent = new Intent(Intent.ACTION_VIEW, selectedPost.getRedirectUri());
        fragment.startActivity(intent);
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
        view.setAdapter(this);
    }

    public static class ViewHolder {

        final TextView txtUser;
        final TextView txtBody;
        final TextView txtDate;
        final ImageView imgPlatform;

        public ViewHolder(View view) {
            txtUser = view.findViewById(R.id.txtUser);
            txtBody = view.findViewById(R.id.txtBody);
            txtDate = view.findViewById(R.id.txtDate);
            imgPlatform = view.findViewById(R.id.imgPlatform);
        }

    }

}

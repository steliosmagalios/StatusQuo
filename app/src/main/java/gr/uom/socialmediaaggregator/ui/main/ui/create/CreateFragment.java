package gr.uom.socialmediaaggregator.ui.main.ui.create;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import gr.uom.socialmediaaggregator.R;
import gr.uom.socialmediaaggregator.data.SocialMediaPlatform;

public class CreateFragment extends Fragment {

    private CreateViewModel viewModel;

    private ToggleButton togglePostType;
    private EditText txtPostText;
    private ImageButton btnSelectImage;
    private TextView txtSelectedName;
    private Switch switchTwitter;
    private Switch switchFacebook;
    private Switch switchInstagram;
    private Button btnPublish;

    private void initComponents(View view) {
        togglePostType = view.findViewById(R.id.togglePostType);
        txtPostText = view.findViewById(R.id.txtPostText);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        txtSelectedName = view.findViewById(R.id.txtSelectedName);
        switchTwitter = view.findViewById(R.id.switchTwitter);
        switchFacebook = view.findViewById(R.id.switchFacebook);
        switchInstagram = view.findViewById(R.id.switchInstagram);
        btnPublish = view.findViewById(R.id.btnPublish);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);
        viewModel = new ViewModelProvider(this).get(CreateViewModel.class);
        initComponents(view);

        switchTwitter.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.changePlatformStatus(SocialMediaPlatform.Twitter, isChecked));
        switchFacebook.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.changePlatformStatus(SocialMediaPlatform.Facebook, isChecked));
        switchInstagram.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.changePlatformStatus(SocialMediaPlatform.Instagram, isChecked));

        togglePostType.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.updateStoryState(isChecked));


        viewModel.getIsStorySelected().observe(getViewLifecycleOwner(), state -> {
            // Update text visibility
            txtPostText.setVisibility(state ? View.GONE : View.VISIBLE);
        });

        return view;
    }

}
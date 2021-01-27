package gr.uom.socialmediaaggregator.ui.main.ui.create;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import gr.uom.socialmediaaggregator.R;
import gr.uom.socialmediaaggregator.api.tasks.CreateAndPublishPost;
import gr.uom.socialmediaaggregator.data.SocialMediaPlatform;

public class CreateFragment extends Fragment {

    public static final String TAG = "SMA";
    public static final int SELECT_IMAGE_REQUEST_CODE = 100;
    private CreateViewModel viewModel;

    private ToggleButton togglePostType;
    private EditText txtPostText;
    private ImageButton btnSelectImage;
    private ImageView imgSelectedImage;
    private TextView txtSelectedName;
    private Switch switchTwitter;
    private Switch switchFacebook;
    private Switch switchInstagram;
    private Button btnPublish;

    private void initComponents(View view) {
        togglePostType = view.findViewById(R.id.togglePostType);
        txtPostText = view.findViewById(R.id.txtPostText);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        imgSelectedImage = view.findViewById(R.id.imgSelectedImage);
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

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE);
        });

        btnPublish.setOnClickListener(v -> {
            String body = txtPostText.getText().toString();
            Uri imgUri = viewModel.getSelectedImageUri().getValue();

            try {
                InputStream imgStream = imgUri != null ? getContext().getContentResolver().openInputStream(imgUri) : null;
                new CreateAndPublishPost(body, imgStream, new ArrayList<>()).execute(); // TODO: 15-Jan-21 Replace with actual SocialMediaPlatforms
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        viewModel.getPublishMedia().observe(getViewLifecycleOwner(), mediaMap -> {
            btnPublish.setEnabled(mediaMap.containsValue(true));
        });

        viewModel.getIsStorySelected().observe(getViewLifecycleOwner(), state -> {
            // Update text visibility
            txtPostText.setVisibility(state ? View.GONE : View.VISIBLE);
        });

        viewModel.getSelectedImageUri().observe(getViewLifecycleOwner(), uri -> {
            // Update the image in the imageview and make it visible
            imgSelectedImage.setImageURI(uri);
            imgSelectedImage.setVisibility(uri != null ? View.VISIBLE : View.GONE);

            // Set the name of the image
            if (uri != null) {
                String name = uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1);
                txtSelectedName.setText(name);
            } else {
                txtSelectedName.setText(R.string.add_an_image);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SELECT_IMAGE_REQUEST_CODE) {
            viewModel.updateImageUri(
                resultCode == Activity.RESULT_OK && data != null ? data.getData() : null
            );
        }
    }
}
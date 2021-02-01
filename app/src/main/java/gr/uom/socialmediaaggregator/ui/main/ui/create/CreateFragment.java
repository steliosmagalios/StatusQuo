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
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import gr.uom.socialmediaaggregator.R;
import gr.uom.socialmediaaggregator.api.tasks.PublishPostTask;
import gr.uom.socialmediaaggregator.data.Platform;

public class CreateFragment extends Fragment {

    public static final String IMAGE_MIME_TYPE = "image/*";
    // Get FirebaseStorage
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

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

        switchTwitter.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.changePlatformStatus(Platform.Twitter, isChecked));
        switchFacebook.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.changePlatformStatus(Platform.Facebook, isChecked));
        switchInstagram.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.changePlatformStatus(Platform.Instagram, isChecked));

        togglePostType.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.updateStoryState(isChecked));

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE);
        });

        btnPublish.setOnClickListener(v -> {
            // Get the social media
            List<Platform> platforms = new ArrayList<>();
            viewModel.getPublishMedia().getValue().forEach((platform, isSelected) -> {
                if (isSelected)
                    platforms.add(platform);
            });

            if (platforms.isEmpty()) {
                Toast.makeText(getContext(), "No platforms selected!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Posting...", Toast.LENGTH_SHORT).show();

                // Depending on whether we are publishing a post or a story, act accordingly
                if (viewModel.getIsStorySelected().getValue() != null && !viewModel.getIsStorySelected().getValue()) {
                    String messageBody = txtPostText.getText().toString();
                    try {
                        // Try to upload the image (if it exists) and publish the posts.
                        uploadImageToFirebase(mediaUri -> {
                            PublishPostTask task = new PublishPostTask(messageBody, mediaUri, platforms);
                            task.addCallback(nothing -> {
                                Toast.makeText(view.getContext(), "Posted!", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(view).navigate(R.id.nav_home);
                            });
                            task.execute();
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    platforms.forEach(platform -> {
                        // Sadgely, stories only work on Instagram and can be shared to Facebook, but not Twitter
                        if (platform == Platform.Instagram) {
                            Intent igIntent = new Intent("com.instagram.share.ADD_TO_STORY");
                            igIntent.setDataAndType(
                                    viewModel.getSelectedImageUri().getValue(),
                                    IMAGE_MIME_TYPE
                            );
                            igIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(igIntent);
                        }
                    });
                }
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

    // Upload image to firebase and returns the download url of the image
    private void uploadImageToFirebase(Consumer<Uri> callback) throws FileNotFoundException {
        if (viewModel.getSelectedImageUri().getValue() != null) {
            Uri imageUri = viewModel.getSelectedImageUri().getValue();
            InputStream imgStream = getContext().getContentResolver().openInputStream(imageUri);

            UUID imageUUID = UUID.randomUUID(); // Give a unique name to the image
            StorageReference imgRef = storage.getReference().child("images/" + imageUUID.toString());
            UploadTask imgUploadTask = imgRef.putStream(imgStream);
            imgUploadTask.continueWithTask(task -> {
                if (!task.isSuccessful())
                    throw task.getException();
                return imgRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    callback.accept(task.getResult());
                }
            });
        } else {
            callback.accept(null);
        }
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
package gr.uom.socialmediaaggregator.ui.main.ui.create;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

import gr.uom.socialmediaaggregator.data.Platform;

public class CreateViewModel extends ViewModel {

    private MutableLiveData<Map<Platform, Boolean>> publishMedia;
    private MutableLiveData<Boolean> isStorySelected;
    private MutableLiveData<Uri> selectedImageUri;

    public CreateViewModel() {
        publishMedia = new MutableLiveData<>(new HashMap<>());
        for (Platform value : Platform.values())
            publishMedia.getValue().put(value, false);
    }

    public LiveData<Map<Platform, Boolean>> getPublishMedia() {
        return publishMedia;
    }

    public LiveData<Boolean> getIsStorySelected() {
        if (isStorySelected == null)
            isStorySelected = new MutableLiveData<>(false);
        return isStorySelected;
    }

    public LiveData<Uri> getSelectedImageUri() {
        if (selectedImageUri == null)
            selectedImageUri = new MutableLiveData<>();
        return selectedImageUri;
    }

    public void updateImageUri(Uri uri) {
        this.selectedImageUri.setValue(uri);
    }

    public void updateStoryState(Boolean state) {
        isStorySelected.setValue(state);
    }

    public void changePlatformStatus(Platform platform, Boolean state) {
        Map<Platform, Boolean> newMap = new HashMap<>(publishMedia.getValue());
        newMap.put(platform, state);
        publishMedia.setValue(newMap);
    }

}
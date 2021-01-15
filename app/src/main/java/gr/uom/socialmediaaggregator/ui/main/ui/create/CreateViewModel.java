package gr.uom.socialmediaaggregator.ui.main.ui.create;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

import gr.uom.socialmediaaggregator.data.SocialMediaPlatform;

public class CreateViewModel extends ViewModel {

    private final Map<SocialMediaPlatform, Boolean> publishMedia;

    private MutableLiveData<Boolean> isStorySelected;

    public CreateViewModel() {
        publishMedia = new HashMap<>();
        for (SocialMediaPlatform value : SocialMediaPlatform.values()) publishMedia.put(value, false);
    }

    public Map<SocialMediaPlatform, Boolean> getPublishMedia() {
        return publishMedia;
    }

    public LiveData<Boolean> getIsStorySelected() {
        if (isStorySelected == null)
            isStorySelected = new MutableLiveData<>(false);
        return isStorySelected;
    }

    public void updateStoryState(Boolean state) {
        isStorySelected.setValue(state);
    }

    public void changePlatformStatus(SocialMediaPlatform platform, Boolean state) {
        publishMedia.put(platform, state);
        publishMedia.forEach((plat, st) -> Log.d("SMA", plat.toString() + ": " + st.toString()));
    }

}
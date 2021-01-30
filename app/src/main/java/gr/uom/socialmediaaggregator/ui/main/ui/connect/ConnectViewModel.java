package gr.uom.socialmediaaggregator.ui.main.ui.connect;

import androidx.lifecycle.ViewModel;

public class ConnectViewModel extends ViewModel {

    private String[] facebookPermissions = new String[] {
            "instagram_basic",
            "instagram_content_publish",
            "pages_read_engagement",
            "pages_manage_posts",
            "pages_manage_metadata",
            "pages_read_engagement",
            "pages_show_list"
    };


    public ConnectViewModel() {
    }

    public String[] getFacebookPermissions() {
        return facebookPermissions;
    }
}
package gr.uom.socialmediaaggregator.api.wrappers;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import twitter4j.Trend;

public class InstagramWrapper {

    // Endpoint
    private static final String TAG_SEARCH_ID = "/ig_hashtag_search";
    private static final String ACCOUNTS_ID = "/me/accounts";
    private static final String PAGE_ID = "/%s";
    private static final String MEDIA_ID = "/%s";
    private static final String TAG_MEDIA_ID = "/%s/recent_media";

    // Data keys
    public static final String INSTAGRAM_BUSINESS_ACCOUNT_KEY = "instagram_business_account";
    public static final String ID_KEY = "id";
    public static final String DATA_KEY = "data";
    public static final String TAG = "SMA";

    private final AccessToken accessToken;

    public InstagramWrapper(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public String getPageIdFromUser() throws JSONException {
        GraphResponse response = makeGraphRequest(ACCOUNTS_ID);
        return response.getJSONObject().getJSONArray("data").getJSONObject(0).getString("id");
    }

    public String getIGUserFromPage(String pageId) throws JSONException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("fields", INSTAGRAM_BUSINESS_ACCOUNT_KEY);

        GraphResponse response = makeGraphRequest(String.format(PAGE_ID, pageId), parameters);
        return response.getJSONObject().getJSONObject(INSTAGRAM_BUSINESS_ACCOUNT_KEY).getString(ID_KEY);
    }

    public String getTagIdFromQuery(String userId, String query) throws JSONException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("user_id", userId);
        parameters.put("q", query.charAt(0) == '#' ? query.substring(1) : query); // Remove the # when searching on instagram

        GraphResponse response = makeGraphRequest(TAG_SEARCH_ID, parameters);
        return response.getJSONObject().getJSONArray(DATA_KEY).getJSONObject(0).getString(ID_KEY);
    }

    public JSONArray fetchPostsWithTrend(Trend trend) throws JSONException {
        String pageId = getPageIdFromUser();
        String userId = getIGUserFromPage(pageId);
        String tagId = getTagIdFromQuery(userId, trend.getName());

        // Get the media from the tagId
        Map<String, String> paramsList = new HashMap<>();
        paramsList.put("user_id", userId);
        paramsList.put("fields", "id,permalink,caption,timestamp");

        GraphResponse response = makeGraphRequest(String.format(TAG_MEDIA_ID, tagId), paramsList);
        JSONArray arr = response.getJSONObject().getJSONArray(DATA_KEY);

        for (int i = 0; i < arr.length(); i++) {
            Log.d(TAG, arr.getJSONObject(i).toString(2));
        }

        return arr;
    }

    private GraphResponse makeGraphRequest(String endpoint) {
        return makeGraphRequest(endpoint, null);
    }

    private GraphResponse makeGraphRequest(String endpoint, Map<String, String> parameters) {
        GraphRequest request = GraphRequest.newGraphPathRequest(accessToken, endpoint, null);

        // If we have parameters, put them in the request
        if (parameters != null && !parameters.isEmpty()) {
            Bundle params = new Bundle();
            parameters.forEach(params::putString);
            request.setParameters(params);
        }

        // Execute it
        return request.executeAndWait();
    }
}

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

    // Endpoints
    private static final String PAGE_ID_TEMPLATE = "/%s";
    private static final String ACCOUNTS_ID = "/me/accounts";
    private static final String TAG_SEARCH_ID = "/ig_hashtag_search";
    private static final String TAG_MEDIA_ID_TEMPLATE = "/%s/recent_media";

    // Keys
    private static final String Q = "q";
    private static final String ID = "id";
    private static final String DATA = "data";
    private static final String FIELDS = "fields";
    private static final String USER_ID = "user_id";
    private static final String INSTAGRAM_BUSINESS_ACCOUNT_KEY = "instagram_business_account";

    public static final String TAG = "SMA";

    // Singleton stuff
    private static InstagramWrapper instance;

    public static InstagramWrapper init(AccessToken accessToken) {
        instance = new InstagramWrapper(accessToken);
        return instance;
    }

    public static InstagramWrapper getInstance() {
        return instance;
    }


    private final AccessToken accessToken;

    private InstagramWrapper(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    private String getPageIdFromUser() throws JSONException {
        GraphResponse response = makeGraphRequest(ACCOUNTS_ID);
        return response.getJSONObject().getJSONArray(DATA).getJSONObject(0).getString(ID);
    }

    private String getIGUserFromPage(String pageId) throws JSONException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(FIELDS, INSTAGRAM_BUSINESS_ACCOUNT_KEY);

        GraphResponse response = makeGraphRequest(String.format(PAGE_ID_TEMPLATE, pageId), parameters);
        return response.getJSONObject().getJSONObject(INSTAGRAM_BUSINESS_ACCOUNT_KEY).getString(ID);
    }

    private String getTagIdFromQuery(String userId, String query) throws JSONException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(USER_ID, userId);
        parameters.put(Q, query.charAt(0) == '#' ? query.substring(1) : query); // Remove the # when searching on instagram

        GraphResponse response = makeGraphRequest(TAG_SEARCH_ID, parameters);
        return response.getJSONObject().getJSONArray(DATA).getJSONObject(0).getString(ID);
    }

    public JSONArray fetchPostsWithTrend(Trend trend) throws JSONException {
        String pageId = getPageIdFromUser();
        String userId = getIGUserFromPage(pageId);
        String tagId = getTagIdFromQuery(userId, trend.getName());

        // Get the media from the tagId
        Map<String, String> paramsList = new HashMap<>();
        paramsList.put(USER_ID, userId);
        paramsList.put(FIELDS, "id,permalink,caption,timestamp");

        GraphResponse response = makeGraphRequest(String.format(TAG_MEDIA_ID_TEMPLATE, tagId), paramsList);
        JSONArray arr = response.getJSONObject().getJSONArray(DATA);

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

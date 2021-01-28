package gr.uom.socialmediaaggregator.api.wrappers;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FacebookWrapper {

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
    private static FacebookWrapper instance;

    public static FacebookWrapper init(AccessToken accessToken) {
        instance = new FacebookWrapper(accessToken);
        return instance;
    }

    public static FacebookWrapper getInstance() {
        return instance;
    }


    private final AccessToken accessToken;
    private AccessToken pageAccessToken;

    private String fbPageId;
    private String igUserId;

    private FacebookWrapper(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    private String getPageIdFromUser() throws JSONException {
        GraphResponse response = makeGraphRequest(ACCOUNTS_ID);
        JSONObject page = response.getJSONObject().getJSONArray(DATA).getJSONObject(0);

        pageAccessToken = new AccessToken(page.getString("access_token"), accessToken.getApplicationId(), accessToken.getUserId(), accessToken.getPermissions(), accessToken.getDeclinedPermissions(), accessToken.getSource(), accessToken.getExpires(), accessToken.getLastRefresh(), accessToken.getDataAccessExpirationTime());
        return page.getString(ID);
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

    public void getUserCredentials() throws JSONException {
        fbPageId = getPageIdFromUser();
        igUserId = getIGUserFromPage(fbPageId);
    }

    public JSONArray getInstagramPostsWithTrend(String trend) throws JSONException {
        String tagId = getTagIdFromQuery(igUserId, trend);

        // Get the media from the tagId
        Map<String, String> paramsList = new HashMap<>();
        paramsList.put(USER_ID, igUserId);
        paramsList.put(FIELDS, "id,permalink,caption,timestamp");

        GraphResponse response = makeGraphRequest(String.format(TAG_MEDIA_ID_TEMPLATE, tagId), paramsList);
        JSONArray arr = response.getJSONObject().getJSONArray(DATA);

        for (int i = 0; i < arr.length(); i++) {
            Log.d(TAG, arr.getJSONObject(i).toString(2));
        }

        return arr;
    }

    public GraphResponse publishPostToFacebookPage(String message, String imageUrl) {
        try {
            JSONObject body = new JSONObject();
            body.put("message", message);

            GraphRequest request;
            if (imageUrl != null) {
                body.put("url", imageUrl);
                request = GraphRequest.newPostRequest(pageAccessToken, String.format("%s/photos", fbPageId), body, null);
            } else {
                request = GraphRequest.newPostRequest(pageAccessToken, String.format("%s/feed", fbPageId), body, null);
            }

            return request.executeAndWait();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GraphResponse publishPostToInstagram(String message, String imageUrl) {
        try {
            JSONObject parameters;

            // Publish and get the container
            parameters = new JSONObject();
            parameters.put("image_url", imageUrl);
            parameters.put("caption", message);

            GraphResponse mediaResponse = GraphRequest.newPostRequest(pageAccessToken, String.format("%s/media", igUserId), parameters, null).executeAndWait();

            // Publish the post
            String postId = mediaResponse.getJSONObject().getString("id");
            parameters = new JSONObject();
            parameters.put("creation_id", postId);

            return GraphRequest.newPostRequest(pageAccessToken, String.format("%s/media_publish", igUserId), parameters, null).executeAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
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

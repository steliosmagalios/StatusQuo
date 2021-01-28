package gr.uom.socialmediaaggregator.api.tasks;

import android.os.AsyncTask;

import org.json.JSONException;

import java.util.function.Consumer;

import gr.uom.socialmediaaggregator.api.wrappers.FacebookWrapper;

public class GetFacebookDataTask extends AsyncTask<Void, Void, Void> {

    private final Consumer<Void> callback;

    public GetFacebookDataTask(Consumer<Void> callback) {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            FacebookWrapper.getInstance().getUserCredentials();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        callback.accept(v);
    }
}

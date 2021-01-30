package gr.uom.socialmediaaggregator.ui.main.ui.connect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import gr.uom.socialmediaaggregator.R;

public class ConnectFragment extends Fragment {

    public static final String TAG = "SMA";

    private CallbackManager facebookCallbackManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ConnectViewModel connectViewModel = new ViewModelProvider(this).get(ConnectViewModel.class);
        View view = inflater.inflate(R.layout.fragment_connect, container, false);

        facebookCallbackManager = CallbackManager.Factory.create();
        LoginButton btnFacebookLogin = view.findViewById(R.id.btnFacebookLogin);
        btnFacebookLogin.setFragment(this);

        btnFacebookLogin.setReadPermissions(connectViewModel.getFacebookPermissions());

        LoginManager.getInstance().registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess: Successfully connected Facebook user" + loginResult.getAccessToken().getUserId());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
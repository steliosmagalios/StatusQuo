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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import gr.uom.socialmediaaggregator.R;

public class ConnectFragment extends Fragment {

    public static final String TAG = "SMA";
    private ConnectViewModel connectViewModel;

    private LoginButton btnFacebookLogin;

    private CallbackManager facebookCallbackManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        connectViewModel = new ViewModelProvider(this).get(ConnectViewModel.class);
        View view = inflater.inflate(R.layout.fragment_connect, container, false);

        facebookCallbackManager = CallbackManager.Factory.create();
        btnFacebookLogin = view.findViewById(R.id.btnFacebookLogin);
        btnFacebookLogin.setFragment(this);

        btnFacebookLogin.setReadPermissions("email", "instagram_basic" ,"pages_show_list");

        LoginManager.getInstance().registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Map<String, Object> accTokenToAdd = new HashMap<>();
                accTokenToAdd.put("facebook_access_token", loginResult.getAccessToken().getToken());

                // Add token to Firestore
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore.getInstance().collection("users")
                    .document(currentUser.getUid()).set(accTokenToAdd)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Successfully added Facebook to Firebase!"));
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
        super.onActivityResult(requestCode, resultCode, data);
    }
}
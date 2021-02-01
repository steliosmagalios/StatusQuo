package gr.uom.socialmediaaggregator.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import gr.uom.socialmediaaggregator.R;
import gr.uom.socialmediaaggregator.api.tasks.GetFacebookDataTask;
import gr.uom.socialmediaaggregator.api.tasks.GetTwitterOAuth2TokenTask;
import gr.uom.socialmediaaggregator.api.wrappers.FacebookWrapper;
import gr.uom.socialmediaaggregator.api.wrappers.TwitterWrapper;
import gr.uom.socialmediaaggregator.ui.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    public static final String TWITTER_ACCESS_TOKEN_KEY = "twitter_access_token";
    public static final String TWITTER_ACCESS_TOKEN_SECRET_KEY = "twitter_access_token_secret";
    private LoginViewModel loginViewModel;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Fetch API keys
        fetchAPIKeys();

        // Login bypassing used for development purposes
        findViewById(R.id.btnBypass).setOnClickListener(v -> {
            loginViewModel.login("demo@sma.com", "123456");
        });

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());
            }
            setResult(Activity.RESULT_OK);
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.login(usernameEditText.getText().toString(),
                    passwordEditText.getText().toString());
        });
    }

    private void fetchAPIKeys() {
        // Facebook
        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token != null && !token.isExpired())
            FacebookWrapper.init(token);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUiWithUser(new LoggedInUserView(currentUser.getEmail()));
        }

    }

    private void updateUiWithUser(LoggedInUserView model) { // TODO: 25-Jan-21 Cleanup
        // Get the keys for Twitter and go to the next activity
        db.collection("users").document(mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(task -> {
                    // If a user exists get his keys and add them to twitter
                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        if (snapshot != null) {
                            String accessToken = snapshot.getString(TWITTER_ACCESS_TOKEN_KEY);
                            String accessTokenSecret = snapshot.getString(TWITTER_ACCESS_TOKEN_SECRET_KEY);
                            boolean userExists = accessToken != null && accessTokenSecret != null;
                            TwitterWrapper.init(accessToken, accessTokenSecret, !userExists);
                            if (!userExists) {
                                new GetTwitterOAuth2TokenTask(v -> getFacebookDataAndChangeActivity()).execute();
                            } else {
                                getFacebookDataAndChangeActivity();
                            }
                        } else {
                            TwitterWrapper.init();

                            // Also get the BearerToken
                            new GetTwitterOAuth2TokenTask(v -> getFacebookDataAndChangeActivity()).execute();
                        }
                    }
                });
    }

    private void getFacebookDataAndChangeActivity() {
        // Get the necessary credentials from Facebook and go to the next activity
        new GetFacebookDataTask(v1 -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }).execute();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
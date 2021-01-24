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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import gr.uom.socialmediaaggregator.R;
import gr.uom.socialmediaaggregator.api.wrappers.TwitterWrapper;
import gr.uom.socialmediaaggregator.data.SocialMediaPlatform;
import gr.uom.socialmediaaggregator.ui.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "SMA";
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

        // Login bypassing used for development purposes
        findViewById(R.id.btnBypass).setOnClickListener( v -> {
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

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUiWithUser(new LoggedInUserView(currentUser.getEmail()));
        }

    }

    private void updateUiWithUser(LoggedInUserView model) {
        // Get the API and User keys
        db.collection("api-keys").get()
            .addOnCompleteListener(taskKeys -> {
                if (taskKeys.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : taskKeys.getResult()) {
                        switch (SocialMediaPlatform.valueOf(doc.getId())) {
                            case Twitter:
                                TwitterWrapper.setApiKeys(doc.getString("api_key"), doc.getString("api_key_secret"));
                            case Facebook:
                                break;
                        }
                    }

                    db.collection("users").document(mAuth.getCurrentUser().getUid())
                        .get().addOnCompleteListener(taskUser -> {
                            if (taskUser.isSuccessful()) {
                                DocumentSnapshot result = taskUser.getResult();
                                TwitterWrapper.setUserKeys(result.getString("twitter_access_token"), result.getString("twitter_access_token_secret"));

                                Intent intent = new Intent(this, MainActivity.class);
                                startActivity(intent);
                            }
                    });
                }
            });
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
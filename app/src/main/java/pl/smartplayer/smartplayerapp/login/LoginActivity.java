package pl.smartplayer.smartplayerapp.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.smartplayer.smartplayerapp.R;
import pl.smartplayer.smartplayerapp.api.ApiClient;
import pl.smartplayer.smartplayerapp.api.TeamService;
import pl.smartplayer.smartplayerapp.api.UserService;
import pl.smartplayer.smartplayerapp.main.MainActivity;
import pl.smartplayer.smartplayerapp.main.Team;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.email)
    EditText _emailText;
    @BindView(R.id.password)
    EditText _passwordText;
    @BindView(R.id.email_sign_in_button)
    Button _loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
    }

    @Override
    public void onBackPressed() {
        // disable going back to the LoginActivity
        moveTaskToBack(true);
    }

    @OnClick(R.id.email_sign_in_button)
    protected void onClickSignInButton(){
        login();
    }

    public void login() {
        Log.d(TAG, "Login");

/*        if (!validate()) {
            onLoginFailed();
            return;
        }*/

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.login_loading));
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        JSONObject emailAndPasswordBody = new JSONObject();
        emailAndPasswordBody.put("email",email);
        emailAndPasswordBody.put("password",password);

        UserService userService = ApiClient.getClient().create(UserService.class);
        Callback<User> userCallback = new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    onLoginSuccess(response.body().getClubId());
                    progressDialog.dismiss();
                } else {
                    onLoginFailed();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                onLoginFailed();
                progressDialog.dismiss();
            }
        };

        Call<User> call = userService.getToken(emailAndPasswordBody);
        call.enqueue(userCallback);
    }

    public void onLoginSuccess(int clubId) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("sClubId", clubId);
        startActivity(intent);
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), getString(R.string.login_failed), Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(getString(R.string.error_invalid_email));
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError(getString(R.string.error_incorrect_password));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}


package com.example.moses.smartsmssolutions;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    Button loginBtn;
    EditText usernameEditText;
    EditText passwordEditText;

    ActionBar actionBar;

    SharedPreferences sharedPreferences;

    public ProgressDialog dialog;

    public static String SHARED_PREFERENCE_NAME = "user";
    public static String SHARED_PREFERENCE_KEY_USERNAME = "username";
    public static String SHARED_PREFERENCE_KEY_PASSWORD = "password";
    public static String SHARED_PREFERENCE_KEY_BALANCE = "balance";

    public static String USER_BALANCE = "balance";
    public static String LOGIN_TYPE = "login_type";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        // preferences to persistently store userdata for automatic login on subsequent app use
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        // add TextWatchers to make sure that the button is only activated when the text fields
        // has received text inputs
        usernameEditText.addTextChangedListener(this);
        passwordEditText.addTextChangedListener(this);

        loginBtn = findViewById(R.id.login);
        loginBtn.setOnClickListener(this);
        // make login button disabled on default to prevent sending of empty requests
        loginBtn.setEnabled(false);

        // if user data is set in preference then login else stay on this activity
        // Note the TextWatcher methods are called, that is why the method is called after button
        // instance was created
        checkUserDataForLogin();
    }

    private void attemptLogin(final User user) {
        SmartSMSSolutionsApplication.onlineServiceManager.attemptLogin(
                user,
                new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                        if (response.isSuccessful()) {

                            if (response.body() != null && response.body().equals("2905")) {
                                Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();

                                // destroy dialog and enable button
                                dismissDialog();
                                loginBtn.setEnabled(true);
                            } else {
                                float balance = Float.parseFloat(response.body());
                                setUserPreference(user, balance);
                                // destroy dialog and enable button
                                dismissDialog();
                                loginBtn.setEnabled(true);

                                gotoHomeActivity(balance, LoginType.NEW);
                            }

                        } else {
                                // destroy dialog and enable button
                                dismissDialog();
                                loginBtn.setEnabled(true);

                                Toast.makeText(LoginActivity.this, "Server returned an error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        // destroy dialog and enable button
                        dismissDialog();
                        loginBtn.setEnabled(true);

                        if (t instanceof IOException){
                            Toast.makeText(LoginActivity.this, "Request timeout, please try again later", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(LoginActivity.this, "A unexpected error just occoured", Toast.LENGTH_LONG).show();
                        }                    }
                }
        );

    }

    private void setUserPreference(User user, float balance) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(SHARED_PREFERENCE_KEY_USERNAME, user.getUsername());
        editor.putString(SHARED_PREFERENCE_KEY_PASSWORD, user.getPassword());
        editor.putFloat(SHARED_PREFERENCE_KEY_BALANCE, balance);
        editor.apply();
    }

    private void checkUserDataForLogin() {
        String username = sharedPreferences.getString(SHARED_PREFERENCE_KEY_USERNAME, "");
        String password = sharedPreferences.getString(SHARED_PREFERENCE_KEY_PASSWORD, "");
        float balance = sharedPreferences.getFloat(SHARED_PREFERENCE_KEY_BALANCE,0f);

        // set username
        usernameEditText.setText(username);

        if (password.length() > 0) {
            gotoHomeActivity(balance,LoginType.OLD);
        }
    }

    private void gotoHomeActivity(float balance, LoginType loginType) {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.putExtra(USER_BALANCE, balance);
        intent.putExtra(LOGIN_TYPE,loginType.getValue());
        // ensure that this activity cant be returned to unless by a logout action
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        LoginActivity.this.startActivity(intent);
        finish();
    }

    @SuppressLint("InflateParams")
    public void showDialog(){
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.signing_in));
        dialog.setCancelable(false);

        dialog.show();
    }

    public void dismissDialog(){
        dialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.login:
                // check if internet is available, if not display a toast message to user
                if (SmartSMSSolutionsApplication.networkHelper.isInternetAvailableWithToast()) {
                    User user = new User(
                            usernameEditText.getText().toString(),
                            passwordEditText.getText().toString()
                    );
                    attemptLogin(user);

                    // show dialog and disable button
                    showDialog();
                    loginBtn.setEnabled(false);
                }

        }
    }

    // methods from TextWatcher interface starts here
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.length() > 0 && password.length() > 0) {
            loginBtn.setEnabled(true);
        } else {
            loginBtn.setEnabled(false);
        }
    }
    // methods from TextWatcher interface ends here
}

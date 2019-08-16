package com.example.moses.smartsmssolutions;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    SharedPreferences sharedPreferences;

    EditText senderEditText, recipientsEditText, messageEditText;
    TextView messageTextCount, textViewBalance;
    ImageButton sendBtn;

    ProgressDialog dialog;

    User user;

    float balance;
    LoginType loginType;

    String sender = "";
    String recipients = "";
    String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFERENCE_NAME, MODE_PRIVATE);

        user = new User(
                sharedPreferences.getString(LoginActivity.SHARED_PREFERENCE_KEY_USERNAME, ""),
                sharedPreferences.getString(LoginActivity.SHARED_PREFERENCE_KEY_PASSWORD, "")
        );

        senderEditText = findViewById(R.id.sender);
        recipientsEditText = findViewById(R.id.recipients);
        messageEditText = findViewById(R.id.message);

        senderEditText.addTextChangedListener(this);
        recipientsEditText.addTextChangedListener(this);
        messageEditText.addTextChangedListener(this);

        messageTextCount = findViewById(R.id.message_text_count);

        sendBtn = findViewById(R.id.send);
        sendBtn.setEnabled(false);
        sendBtn.setOnClickListener(this);

        Intent intent = getIntent();
        balance = intent.getFloatExtra(LoginActivity.USER_BALANCE,0f);
        loginType = LoginType.valueOf(intent.getIntExtra(LoginActivity.LOGIN_TYPE, 0));

//        if (loginType == LoginType.OLD){
//            textViewBalance.setText(new Formatter(Locale.getDefault()).format("%f",balance).toString());
//        }else{
//            textViewBalance.setText(new Formatter(Locale.getDefault()).format("%f",balance).toString());
//        }
        invalidateOptionsMenu();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_menu, menu);

        menu.getItem(0).setTitle(new StringBuilder().append(
                new Formatter(Locale.getDefault()).format("%.2f",balance).toString())
                .append(" units"));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                startActivity(new Intent(this, LoginActivity.class));
                // only password is unset because the username will be useful in the future
                unsetPassword();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void unsetPassword() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LoginActivity.SHARED_PREFERENCE_KEY_PASSWORD, "");
        editor.apply();
    }

    public void showDialog(){
        dialog = new ProgressDialog(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.sending_message));
        dialog.setCancelable(false);

        dialog.show();
    }

    public void dismissDialog(){
        dialog.dismiss();
    }


    @Override
    public void onClick(View v) {
        if (SmartSMSSolutionsApplication.networkHelper.isInternetAvailableWithToast()) {
            assert user != null;
            SMS sms = new SMS(
                    user,
                    senderEditText.getText().toString(),
                    recipientsEditText.getText().toString(),
                    messageEditText.getText().toString()
            );
            sendSMS(sms);
        }
        // show dialog and disable button
        showDialog();
        sendBtn.setEnabled(false);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        sender = senderEditText.getText().toString();
        recipients = recipientsEditText.getText().toString();
        message = messageEditText.getText().toString();

        if (sender.length() > 0 && recipients.length() > 0 && message.length() > 0) {
            sendBtn.setEnabled(true);
        } else {
            sendBtn.setEnabled(false);
        }

        if (message != null){
            messageTextCount.setText(getMessageCountString(message));
        }
    }


    private String getMessageCountString(@NonNull String message){
        int messageLength = message.length();
        int page;
        int remainder;
        String messageCountString;

        if (messageLength < 161) {
            page = 1;
            remainder = 160 - messageLength;
        } else if (messageLength < 307) {
            page = 2;
            remainder = (146 + 160) - messageLength;
        } else {
            remainder = (((messageLength - (160 + 146)) % 153)==0)?0:(153 - ((messageLength - (160 + 146)) % 153));
            page = 2 + (messageLength - (160 + 146)) / 153 + ((((remainder % 153) != 0)) ? 1 : 0);
        }

        Formatter formatter = new Formatter(Locale.getDefault());
        if (messageLength > 0) {
            messageCountString = formatter.format("%d / %d / %d", page, remainder, messageLength).toString();
        } else {
            messageCountString = formatter.format("0 / %d / 0", 160).toString();
        }

        return messageCountString;
    }

    public void sendSMS(SMS sms) {
        SmartSMSSolutionsApplication.onlineServiceManager.sendSms(
                sms,
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                        String resp = "";
                        try {
                            if (response.body() != null){
                                resp = response.body().string();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (response.isSuccessful()) {

                            if (resp.length() == 4) {
                                String server_resp="";
                                switch(resp){
                                    case "2904": server_resp = "SMS Sending Failed";
                                        break;
                                    case "2906": server_resp = "Credit Exhausted";
                                        break;
                                    case "2907": server_resp = "Gateway Unavailable";
                                        break;
                                    case "2916": server_resp = "Sender ID not allowed by the operator";
                                        break;
                                }
                                Toast.makeText(HomeActivity.this, server_resp+resp, Toast.LENGTH_LONG).show();
                            } else if (resp.length() > 4) {
                                String server_resp[] = resp.split(" ");
                                Toast.makeText(HomeActivity.this, "Message sent "+ server_resp[1]+" credit used", Toast.LENGTH_LONG).show();
                            }
                            // destroy dialog and enable button
                            dismissDialog();
                            sendBtn.setEnabled(true);

                        } else {
                            Toast.makeText(HomeActivity.this, "Server returned an error", Toast.LENGTH_SHORT).show();

                            // destroy dialog and enable button
                            dismissDialog();

                        }


                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        // destroy dialog and enable button
                        dismissDialog();
                        sendBtn.setEnabled(true);
                        if (t instanceof IOException){
                            Toast.makeText(HomeActivity.this, "Request timeout, please try again later", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(HomeActivity.this, "A unexpected error just occoured", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

}

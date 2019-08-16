package com.example.moses.smartsmssolutions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OnlineServiceManager {

    private static OnlineServices onlineServices;
    private static OnlineServiceManager onlineServiceManager;


    private OnlineServiceManager(){

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.smartsmssolutions.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        onlineServices = retrofit.create(OnlineServices.class);
    }

    public static OnlineServiceManager getInstance(){
        if (onlineServiceManager == null){
            onlineServiceManager = new OnlineServiceManager();
        }
        return onlineServiceManager;

    }

    public void attemptLogin(User user, Callback<String> callback){
        Map<String, String> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("password", user.getPassword());
        data.put("balance", "true");

        Call<String> loginCall = onlineServices.login(data);
        loginCall.enqueue(callback);
    }


    public void sendSms(SMS sms, Callback<ResponseBody> callback){

        Call<ResponseBody> sendSms = onlineServices.sendSms(
                sms.getUsername(),
                sms.getPassword(),
                sms.getMessage(),
                sms.getSender(),
                sms.getRecipient()
        );
        sendSms.enqueue(callback);
    }
}

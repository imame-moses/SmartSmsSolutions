package com.example.moses.smartsmssolutions;

import java.util.Map;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface  OnlineServices {

    @GET("smsapi.php")
    Call<String> login(@QueryMap Map<String, String> options);

    @FormUrlEncoded
    @POST("smsapi.php")
    Call<ResponseBody> sendSms(
            @Field("username") String username,
            @Field("password") String password,
            @Field("message") String message,
            @Field("sender") String sender,
            @Field("recipient") String recipient
            );
}


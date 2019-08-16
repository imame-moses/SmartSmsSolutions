package com.example.moses.smartsmssolutions;

import android.app.Application;

public class SmartSMSSolutionsApplication extends Application {

    public static OnlineServiceManager onlineServiceManager;
    public static NetworkHelper networkHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        onlineServiceManager = OnlineServiceManager.getInstance();
        networkHelper = new NetworkHelper(this);

    }
}

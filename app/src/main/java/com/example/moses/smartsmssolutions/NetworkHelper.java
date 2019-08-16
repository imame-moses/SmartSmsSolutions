package com.example.moses.smartsmssolutions;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class NetworkHelper{
    private Application app;

    public NetworkHelper(Application application){
        app=application;
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager= (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() !=null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
    public boolean isInternetAvailableWithToast() {
        ConnectivityManager connectivityManager= (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean val=connectivityManager.getActiveNetworkInfo() !=null && connectivityManager.getActiveNetworkInfo().isConnected();
        if (val){
            return true;
        }else{
            Toast.makeText(app, "No Internet Available {NH}", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}

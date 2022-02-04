package com.example.thundervpn.activity;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.thundervpn.R;
import com.example.thundervpn.utils.Constant;
import com.example.thundervpn.utils.SharedPref;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {

    private SharedPref sharedPref;

    @Override
    public void onCreate() {


        super.onCreate();

        FirebaseApp.initializeApp(this);

        sharedPref = new SharedPref(this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        Constant.PACKAGE_NAME = getApplicationContext().getPackageName();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "thundervpn_channel";
            String description = "thundervpn_des_channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Constant.NOTI_CHANNEL, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }
}

package com.example.thundervpn.utils;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.thundervpn.activity.SplashActivity;
import com.example.thundervpn.listeners.MyListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import org.jetbrains.annotations.NotNull;

public class AppOpenAdsManager {

    private static final String AD_UNIT_ID = Constant.OPENAPP_ADS_ID;
    private static AppOpenAd mAppOpenAd;

    private final Context context;
    private final MyListener listener;

    @RequiresApi(api = Build.VERSION_CODES.P)
    public AppOpenAdsManager(Context context, MyListener listener) {
        this.context = context;
        this.listener = listener;
        fetchAd();

    }

    public void fetchAd() {
        if(isAdAvailable()){
            return;
        }
        AdRequest request = getAdRequest();
        AppOpenAd.AppOpenAdLoadCallback loadCallback;
        AppOpenAd.load(
                context, AD_UNIT_ID, request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull @NotNull AppOpenAd appOpenAd) {
                        mAppOpenAd = appOpenAd;
                        super.onAdLoaded(appOpenAd);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                    }
                }
        );
    };

    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    public boolean isAdAvailable() {
        if(mAppOpenAd != null){
            return true;
        }else{
            return false;
        }
    }

    public void showAdIfAvailable(){
        if(isAdAvailable()){
            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback(){
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            mAppOpenAd = null;
                            fetchAd();
                            listener.onClick();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                        }
                    };
            mAppOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            mAppOpenAd.show((SplashActivity)context);
        }else{
            fetchAd();
            listener.onClick();
        }
    }
}
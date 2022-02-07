/*
 * *
 *  * Created by tuan on 1/28/22, 7:26 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/28/22, 7:26 PM
 *
 */

package com.example.thundervpn.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.thundervpn.activity.MainActivity;
import com.example.thundervpn.items.MyProxy;
import com.example.thundervpn.listeners.MyListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Methods {
    private Context context;
    private InterstitialAd mInterstitialAd;

    public Methods(Context context) {
        this.context = context;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public String base64Encode(String input){
        byte[] encodeValue = Base64.encode(input.getBytes(), Base64.DEFAULT);
        return (new String(encodeValue)).trim();
    }

    public String base64Decode(String input) throws UnsupportedEncodingException {
        byte[] encodeValue = Base64.decode(input, Base64.DEFAULT);
        return (new String(encodeValue, "UTF-8")).trim();
    }

    public void showInterAds(MyListener listener){
        if(Constant.isInterAd){
            Constant.adCount++;
            if(Constant.adCount % Constant.adShow == 0 && !Constant.IS_PREMIUM){
                AdRequest adRequest = new AdRequest.Builder().build();

                InterstitialAd.load(context,Constant.INTER_ADS_ID, adRequest,
                        new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                // The mInterstitialAd reference will be null until
                                // an ad is loaded.
                                mInterstitialAd = interstitialAd;

                                if(mInterstitialAd != null){

                                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                                        @Override
                                        public void onAdDismissedFullScreenContent() {
                                            // Called when fullscreen content is dismissed.
                                            listener.onClick();
                                            Log.d("TAG", "The ad was dismissed.");
                                        }

                                        @Override
                                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                                            // Called when fullscreen content failed to show.
                                            listener.onClick();
                                            Log.d("TAG", "The ad failed to show.");
                                        }

                                        @Override
                                        public void onAdShowedFullScreenContent() {
                                            // Called when fullscreen content is shown.
                                            // Make sure to set your reference to null so you don't
                                            // show it a second time.
                                            mInterstitialAd = null;
                                            Log.d("TAG", "The ad was shown.");
                                        }
                                    });

                                    mInterstitialAd.show((Activity)context);
                                }else {
                                    listener.onClick();
                                }
                                Log.i("TAG", "onAdLoaded");

                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                // Handle the error
                                Log.i("TAG", loadAdError.getMessage());

                                listener.onClick();

                                mInterstitialAd = null;
                            }
                        });
            }else {
                listener.onClick();
            }
        }else {
            listener.onClick();
        }
    }

    public MyProxy getDefaultProxy(){

        String proxy_str = Constant.DEFAULT_PROXY;

        String[] arr = proxy_str.split(":");

        String hostname = arr[0];
        int port = Integer.parseInt(arr[1]);
        String username = arr[2];
        String password = arr[3];

        return new MyProxy(0, hostname, port, username, password, 0);
    }

    public boolean checkForEncode(String string) {
        String pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(string);
        return m.find();
    }

    public boolean isAppRunning(final Context context) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(Constant.PACKAGE_NAME)) {
                    return true;
                }
            }
        }
        return false;
    }

    public RequestBody getRequestBody(String method_name, Bundle bundle){
        JsonObject postObj = new JsonObject();

        postObj.addProperty("method_name", method_name);

        switch (method_name){
            case "method_signup":
                postObj.addProperty("uid", base64Encode(bundle.getString("uid")));
                postObj.addProperty("name", base64Encode(bundle.getString("name")));
                postObj.addProperty("email", base64Encode(bundle.getString("email")));
                postObj.addProperty("date", bundle.getString("date"));
                break;

            case "method_get_proxy":
                postObj.addProperty("id", bundle.getInt("id"));
                break;

            case "method_get_user":
                postObj.addProperty("uid", base64Encode(bundle.getString("uid")));
                break;
            case "method_change_name":
                postObj.addProperty("name", base64Encode(bundle.getString("name")));
                postObj.addProperty("uid", base64Encode(bundle.getString("uid")));
                break;
            case "method_send_feedback":
                postObj.addProperty("uid", base64Encode(bundle.getString("uid")));
                postObj.addProperty("msg", base64Encode(bundle.getString("msg")));
                break;
        }

        String post_data = postObj.toString();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("data", post_data);

        return builder.build();
    }
}

/*
 * *
 *  * Created by tuan on 1/28/22, 9:25 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/28/22, 9:25 PM
 *
 */

package com.example.thundervpn.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.thundervpn.asynctasks.ExecuteQueryAsync;
import com.example.thundervpn.asynctasks.GetAdminSettingsAsync;
import com.example.thundervpn.asynctasks.GetUserAsync;
import com.example.thundervpn.databinding.ActivitySplashBinding;
import com.example.thundervpn.items.MyUser;
import com.example.thundervpn.listeners.ExecuteQueryListener;
import com.example.thundervpn.listeners.GetUserListener;
import com.example.thundervpn.listeners.MyListener;
import com.example.thundervpn.utils.AppOpenAdsManager;
import com.example.thundervpn.utils.Constant;
import com.example.thundervpn.utils.Methods;
import com.example.thundervpn.utils.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private SharedPref sharedPref;
    private Methods methods;
    private FirebaseAuth auth;
    private boolean isAutoLogin = false;
    private boolean isShowOpenAds = true;
    private AppOpenAdsManager appOpenAdsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        methods = new Methods(this);
        sharedPref = new SharedPref(this);
        auth = FirebaseAuth.getInstance();

        appOpenAdsManager = new AppOpenAdsManager(this, new MyListener() {
            @Override
            public void onClick() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        });

        LoadSetting();

    }

    private void checkLoginState(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sharedPref.getIsAutoLogin()) {
                    if(methods.isNetworkConnected()){

                        String uid = auth.getCurrentUser().getUid();
                        Bundle bundle = new Bundle();
                        bundle.putString("uid", uid);

                        GetUserAsync async = new GetUserAsync(methods.getRequestBody("method_get_user", bundle), methods, new GetUserListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onEnd(boolean status, MyUser user) {
                                if(methods.isNetworkConnected()){
                                    if(status){
                                        isShowOpenAds = user.getExpired_date().before(new Date());
                                    }
                                    isAutoLogin = true;
                                    autoLogin();
                                }else{
                                    isAutoLogin = true;
                                    autoLogin();
                                }
                            }
                        });

                        async.execute();

                    }else {
                        Toast.makeText(SplashActivity.this, Constant.ERROR_INTERNET, Toast.LENGTH_SHORT).show();
                        Constant.isLogged = false;
                        appOpenAdsManager.showAdIfAvailable();
                    }
                } else {
                    isAutoLogin = false;
                    Constant.isLogged = false;
                    appOpenAdsManager.showAdIfAvailable();
                }
            }
        }, 1200);
    }

    private void openMainActivity(){
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void autoLogin(){
        auth.signInWithEmailAndPassword(sharedPref.getEmail(), sharedPref.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            if(auth.getCurrentUser().isEmailVerified()){
                                Constant.isLogged = true;
                                Constant.UID = auth.getCurrentUser().getUid();

                                if(isShowOpenAds){
                                    appOpenAdsManager.showAdIfAvailable();
                                }else{
                                    openMainActivity();
                                }

                            }else{
                                Toast.makeText(SplashActivity.this, "Your email is not verified! Please verify your email!", Toast.LENGTH_SHORT).show();
                                Constant.isLogged = false;
                                Toast.makeText(SplashActivity.this, "Unauthorized Access", Toast.LENGTH_SHORT).show();

                                if(isShowOpenAds){
                                    appOpenAdsManager.showAdIfAvailable();
                                }else{
                                    openMainActivity();
                                }
                            }
                        }else{
                            Constant.isLogged = false;
                            Toast.makeText(SplashActivity.this, "Unauthorized Access", Toast.LENGTH_SHORT).show();

                            if(isShowOpenAds){
                                appOpenAdsManager.showAdIfAvailable();
                            }else{
                                openMainActivity();
                            }
                        }
                    }
                });
    }

    private void LoadSetting(){
         GetAdminSettingsAsync async = new GetAdminSettingsAsync(methods, methods.getRequestBody("method_get_settings", null), new ExecuteQueryListener() {
             @Override
             public void onStart() {

             }

             @Override
             public void onEnd(boolean status) {
                 if(methods.isNetworkConnected()){
                     if(status){
                         checkLoginState();
                     }else{
                         Toast.makeText(SplashActivity.this, Constant.ERROR_MSG, Toast.LENGTH_SHORT).show();

                         new Handler().postDelayed(new Runnable() {
                             @Override
                             public void run() {
                                 finishAffinity();
                             }
                         }, 1000);

                     }
                 }else{
                     Toast.makeText(SplashActivity.this, Constant.ERROR_INTERNET, Toast.LENGTH_SHORT).show();

                     new Handler().postDelayed(new Runnable() {
                         @Override
                         public void run() {
                             finishAffinity();
                         }
                     }, 1000);
                 }
             }
         });

         async.execute();
    }
}
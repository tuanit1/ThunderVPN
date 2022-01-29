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

import com.example.thundervpn.databinding.ActivitySplashBinding;
import com.example.thundervpn.utils.Constant;
import com.example.thundervpn.utils.Methods;
import com.example.thundervpn.utils.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private SharedPref sharedPref;
    private Methods methods;
    private FirebaseAuth auth;
    private boolean isAutoLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        methods = new Methods(this);
        sharedPref = new SharedPref(this);
        auth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sharedPref.getIsAutoLogin()) {
                    if(methods.isNetworkConnected()){
                        isAutoLogin = true;
                        autoLogin();
                    }else {
                        Toast.makeText(SplashActivity.this, Constant.ERROR_INTERNET, Toast.LENGTH_SHORT).show();
                        Constant.isLogged = false;
                        openMainActivity();
                    }
                } else {
                    isAutoLogin = false;
                    Constant.isLogged = false;
                    openMainActivity();
                }
            }
        }, 2000);

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
                                openMainActivity();

                                //appOpenAdsManager.showAdIfAvailable();
                            }else{
                                Toast.makeText(SplashActivity.this, "Your email is not verified! Please verify your email!", Toast.LENGTH_SHORT).show();
                                Constant.isLogged = false;
                                Toast.makeText(SplashActivity.this, "Unauthorized Access", Toast.LENGTH_SHORT).show();
                                openMainActivity();
                            }
                        }else{
                            Constant.isLogged = false;
                            Toast.makeText(SplashActivity.this, "Unauthorized Access", Toast.LENGTH_SHORT).show();
                            openMainActivity();
                        }
                    }
                });
    }
}
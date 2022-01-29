/*
 * *
 *  * Created by tuan on 1/29/22, 12:22 AM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/29/22, 12:22 AM
 *
 */

package com.example.thundervpn.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.thundervpn.R;
import com.example.thundervpn.databinding.ActivityMainBinding;
import com.example.thundervpn.databinding.ActivityMyAccountBinding;
import com.example.thundervpn.utils.Constant;
import com.example.thundervpn.utils.SharedPref;
import com.google.firebase.auth.FirebaseAuth;

public class MyAccountActivity extends AppCompatActivity {

    private ActivityMyAccountBinding binding;
    private FirebaseAuth auth;
    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyAccountBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();
        sharedPref = new SharedPref(this);

        setupUI();
    }

    private void setupUI() {

        if(Constant.isLogged){
            binding.loggedView.setVisibility(View.VISIBLE);
            binding.nologgedView.setVisibility(View.GONE);
        }else{
            binding.loggedView.setVisibility(View.GONE);
            binding.nologgedView.setVisibility(View.VISIBLE);
        }

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyAccountActivity.this, LoginActivity.class));
            }
        });
    }

    private void logOut(){
        auth.signOut();
        Constant.isLogged = false;
        sharedPref.setIsAutoLogin(false);

        Toast.makeText(MyAccountActivity.this, "You signed out", Toast.LENGTH_SHORT).show();

        binding.loggedView.setVisibility(View.GONE);
        binding.nologgedView.setVisibility(View.VISIBLE);
    }


}
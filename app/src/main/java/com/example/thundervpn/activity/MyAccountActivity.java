/*
 * *
 *  * Created by tuan on 1/29/22, 12:22 AM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/29/22, 12:22 AM
 *
 */

package com.example.thundervpn.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thundervpn.R;
import com.example.thundervpn.asynctasks.ExecuteQueryAsync;
import com.example.thundervpn.asynctasks.GetUserAsync;
import com.example.thundervpn.databinding.ActivityMainBinding;
import com.example.thundervpn.databinding.ActivityMyAccountBinding;
import com.example.thundervpn.items.MyUser;
import com.example.thundervpn.listeners.ExecuteQueryListener;
import com.example.thundervpn.listeners.GetUserListener;
import com.example.thundervpn.utils.Constant;
import com.example.thundervpn.utils.Methods;
import com.example.thundervpn.utils.SharedPref;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyAccountActivity extends AppCompatActivity {

    private ActivityMyAccountBinding binding;
    private FirebaseAuth auth;
    private Methods methods;
    private SharedPref sharedPref;
    private MyUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyAccountBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();
        sharedPref = new SharedPref(this);
        methods = new Methods(this);

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyAccountActivity.this, LoginActivity.class));
            }
        });

        if(Constant.isLogged){
            LoadUser();
        }else{
            binding.loggedView.setVisibility(View.GONE);
            binding.rlEmpty.setVisibility(View.GONE);
            binding.nologgedView.setVisibility(View.VISIBLE);
        }
    }

    private void LoadUser(){
        String uid = auth.getCurrentUser().getUid();
        Bundle bundle = new Bundle();
        bundle.putString("uid", uid);

        GetUserAsync async = new GetUserAsync(methods.getRequestBody("method_get_user", bundle), methods, new GetUserListener() {
            @Override
            public void onStart() {
                binding.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEnd(boolean status, MyUser user) {

                if(methods.isNetworkConnected()){
                    if(status){
                        mUser = user;
                        setupUI(false, "");
                    }else{
                        Toast.makeText(MyAccountActivity.this, Constant.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        setupUI(true, Constant.ERROR_MSG);
                    }
                }else{
                    Toast.makeText(MyAccountActivity.this, Constant.ERROR_INTERNET, Toast.LENGTH_SHORT).show();
                    setupUI(true, Constant.ERROR_INTERNET);
                }

                binding.progressBar.setVisibility(View.GONE);
            }
        });

        async.execute();
    }


    private void setupUI(boolean isError, String err_msg) {

        if(isError){
            binding.rlEmpty.setVisibility(View.VISIBLE);
            binding.loggedView.setVisibility(View.GONE);
            binding.nologgedView.setVisibility(View.GONE);

            binding.tvEmpty.setText(err_msg);
            binding.btnTry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoadUser();
                }
            });

        }else{
            binding.loggedView.setVisibility(View.VISIBLE);
            binding.nologgedView.setVisibility(View.GONE);
            binding.rlEmpty.setVisibility(View.GONE);

            binding.btnSignout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logOut();
                }
            });

            binding.tvName.setText(mUser.getName());

            if(mUser.getExpired_date().after(new Date())){
                binding.tvType.setText("PREMIUM");
                binding.tvType.setTextColor(getResources().getColor(R.color.golden));
                binding.tvType.setTypeface(binding.tvType.getTypeface(), Typeface.BOLD);
                binding.llExpired.setVisibility(View.VISIBLE);

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, MMMM dd, yyyy");
                binding.tvExpired.setText(sdf.format(mUser.getExpired_date()));
                binding.tvPremium.setText("Buy more");

            }else{
                binding.tvType.setText("FREE");
                binding.llExpired.setVisibility(View.GONE);
                binding.tvPremium.setText("Go Premium");
            }

            binding.btnChangeName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialogText("Change name", mUser.getName());
                }
            });

            binding.btnChangepass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MyAccountActivity.this, ChangePasswordActivity.class));
                }
            });
        }


    }

    private void openDialogText(String name, String temp) {
        Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_inputtext);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText edt_dialog = dialog.findViewById(R.id.edt_dialog);
        TextView tv_dialog = dialog.findViewById(R.id.tv_dialog);
        Button btn_ok = dialog.findViewById(R.id.btn_ok);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edt_dialog.getText().toString().isEmpty()){

                    ChangeName(edt_dialog.getText().toString());

                    dialog.dismiss();

                }else {
                    edt_dialog.setError("Empty!");
                }
            }
        });

        tv_dialog.setText(name);
        edt_dialog.setText(temp);

        dialog.show();
    }

    private void ChangeName(String name) {

        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("uid", auth.getCurrentUser().getUid());

        ExecuteQueryAsync async = new ExecuteQueryAsync(methods.getRequestBody("method_change_name", bundle), new ExecuteQueryListener() {
            @Override
            public void onStart() {
                binding.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        binding.tvName.setText(name);
                        Toast.makeText(getApplicationContext(), "Your name updated!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), Constant.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), Constant.ERROR_INTERNET, Toast.LENGTH_SHORT).show();
                }
                binding.progressBar.setVisibility(View.GONE);

            }
        });

        async.execute();
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
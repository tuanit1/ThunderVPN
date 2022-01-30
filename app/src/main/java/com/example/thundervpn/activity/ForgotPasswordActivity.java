/*
 * *
 *  * Created by ACER on 1/31/22, 12:45 AM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/31/22, 12:45 AM
 *
 */

package com.example.thundervpn.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.ConditionVariable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.example.thundervpn.R;
import com.example.thundervpn.databinding.ActivityChangePasswordBinding;
import com.example.thundervpn.databinding.ActivityForgotPasswordBinding;
import com.example.thundervpn.utils.Constant;
import com.example.thundervpn.utils.Methods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.logging.ConsoleHandler;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private Methods methods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        methods = new Methods(this);

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = binding.edtEmail.getText().toString();

                if(email.isEmpty()){
                    binding.edtEmail.setError("Empty!");
                    return;
                }

                if(methods.isNetworkConnected()){
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgotPasswordActivity.this, "Please verify your email address!", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(ForgotPasswordActivity.this, Constant.ERROR_MSG, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(ForgotPasswordActivity.this, Constant.ERROR_INTERNET, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
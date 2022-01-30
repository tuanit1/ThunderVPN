/*
 * *
 *  * Created by ACER on 1/30/22, 11:51 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/30/22, 11:51 PM
 *
 */

package com.example.thundervpn.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.example.thundervpn.R;
import com.example.thundervpn.databinding.ActivityChangePasswordBinding;
import com.example.thundervpn.databinding.ActivityMainBinding;
import com.example.thundervpn.utils.Constant;
import com.example.thundervpn.utils.LoadingDialog;
import com.example.thundervpn.utils.Methods;
import com.example.thundervpn.utils.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private ActivityChangePasswordBinding binding;
    private FirebaseAuth auth;
    private LoadingDialog loadingDialog;
    private SharedPref sharedPref;
    private Methods methods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();
        sharedPref = new SharedPref(this);
        methods = new Methods(this);
        loadingDialog = new LoadingDialog(this, "Please wait...");

        binding.edtEmail.setText(auth.getCurrentUser().getEmail());

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isPass = true;

                String email = binding.edtEmail.getText().toString();
                String crr_pass = binding.edtCrrPassword.getText().toString();
                String new_pass = binding.edtNewPassword.getText().toString();

                if(email.isEmpty()){
                    binding.edtEmail.setError("Empty!");
                    isPass = false;
                }
                if(crr_pass.isEmpty()){
                    binding.edtCrrPassword.setError("Empty!");
                    isPass = false;
                }
                if(new_pass.isEmpty()){
                    binding.edtNewPassword.setError("Empty!");
                    isPass = false;
                }

                if(isPass){

                    if(!methods.isNetworkConnected()){
                        Toast.makeText(ChangePasswordActivity.this, Constant.ERROR_INTERNET, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    loadingDialog.StartLoadingDialog();

                    FirebaseUser user = auth.getCurrentUser();

                    AuthCredential credential = EmailAuthProvider
                            .getCredential(email, crr_pass);

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        user.updatePassword(new_pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ChangePasswordActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                                                    sharedPref.setPassword(new_pass);
                                                    onBackPressed();
                                                } else {
                                                    Toast.makeText(ChangePasswordActivity.this, Constant.ERROR_MSG, Toast.LENGTH_SHORT).show();
                                                }
                                                loadingDialog.DismissDialog();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(ChangePasswordActivity.this, "Make sure current password is correct!", Toast.LENGTH_SHORT).show();
                                        loadingDialog.DismissDialog();
                                    }
                                }
                            });
                }

            }
        });
    }
}
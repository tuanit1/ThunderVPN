package com.example.thundervpn.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.thundervpn.asynctasks.ExecuteQueryAsync;
import com.example.thundervpn.databinding.ActivityLoginBinding;
import com.example.thundervpn.listeners.ExecuteQueryListener;
import com.example.thundervpn.utils.Constant;
import com.example.thundervpn.utils.LoadingDialog;
import com.example.thundervpn.utils.Methods;
import com.example.thundervpn.utils.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.RequestBody;

/*
 * *
 *  * Created by tuan on 1/28/22, 1:11 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/28/22, 1:11 PM
 *
 */

public class LoginActivity extends AppCompatActivity {

    private Methods methods;
    private SharedPref sharedPref;
    private FirebaseAuth auth;
    private ActivityLoginBinding binding;
    private BottomSheetBehavior bottomSheetBehavior;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();
        methods = new Methods(this);
        sharedPref = new SharedPref(this);
        loadingDialog = new LoadingDialog(this, "Please wait...");

        setupUI();
    }

    private void setupUI(){

        //IS REMEMBER
        if(sharedPref.isRemember()){
            binding.edtEmail.setText(sharedPref.getEmail());
            binding.edtPassword.setText(sharedPref.getPassword());
            binding.ckbRemember.setChecked(true);
        }else {
            binding.ckbRemember.setChecked(false);
        }

        //LOGIN LAYOUT
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginEvent();
            }
        });

        binding.btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        //SIGN UP LAYOUT
        bottomSheetBehavior = BottomSheetBehavior.from(binding.signup.bottomSignupLayout);

        binding.signup.tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        binding.signup.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        binding.signup.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignupEvent();
            }
        });


    }

    private void LoginEvent() {
        String email = binding.edtEmail.getText().toString().trim();
        String pass = binding.edtPassword.getText().toString().trim();

        boolean isPass = true;

        if(email.isEmpty()){
            binding.edtEmail.setError("Email is empty!");
            isPass = false;
        }
        if(pass.isEmpty()){
            binding.edtPassword.setError("Password is empty!");
            isPass = false;
        }

        if(isPass){

            loadingDialog.StartLoadingDialog();

            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        loadingDialog.DismissDialog();
                        if(auth.getCurrentUser().isEmailVerified()){
                            Constant.isLogged = true;
                            Constant.UID = auth.getCurrentUser().getUid();
                            sharedPref.setEmail(email);
                            sharedPref.setPassword(pass);
                            sharedPref.setRemember(binding.ckbRemember.isChecked());
                            sharedPref.setIsAutoLogin(true);

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }else{
                            auth.getCurrentUser().sendEmailVerification();
                            Toast.makeText(LoginActivity.this, "Your email is not verified! Please verify your email!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(LoginActivity.this, "Incorrect email or password!", Toast.LENGTH_SHORT).show();
                        loadingDialog.DismissDialog();
                    }
                }
            });
        }
    }

    private void SignupEvent() {

        String name = binding.signup.edtSignupName.getText().toString().trim();
        String email = binding.signup.edtSignupEmail.getText().toString().trim();
        String pass = binding.signup.edtSignupPassword.getText().toString().trim();

        boolean isPass = true;

        if(name.isEmpty()){
            binding.signup.edtSignupName.setError("Name is required!");
            isPass = false;
        }
        if(email.isEmpty()){
            binding.signup.edtSignupEmail.setError("Email is required!");
            isPass = false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.signup.edtSignupEmail.setError("Please provide a valid email!");
            isPass = false;
        }
        if(pass.isEmpty()){
            binding.signup.edtSignupPassword.setError("Password is required!");
            isPass = false;
        }

        if(pass.length() < 6){
            binding.signup.edtSignupPassword.setError("Password must be at least 6 characters");
            isPass = false;
        }

        if(isPass){

            loadingDialog.StartLoadingDialog();

            auth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if(task.getResult().getSignInMethods().isEmpty()){
                                auth.createUserWithEmailAndPassword(email, pass)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if(task.isSuccessful()){

                                                    String uid = auth.getCurrentUser().getUid();
                                                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("uid", uid);
                                                    bundle.putString("name", name);
                                                    bundle.putString("date", date);

                                                    SignUp(bundle);

                                                }else{
                                                    Toast.makeText(LoginActivity.this, "Failed to sign up! Try again!", Toast.LENGTH_SHORT).show();
                                                    loadingDialog.DismissDialog();
                                                }
                                            }
                                        });
                            }else{
                                Toast.makeText(LoginActivity.this, "This email have been used!", Toast.LENGTH_SHORT).show();
                                loadingDialog.DismissDialog();
                            }
                        }
                    });
        }
    }

    private void SignUp(Bundle bundle) {
        RequestBody requestBody = methods.getRequestBody("method_signup", bundle);

        ExecuteQueryListener listener = new ExecuteQueryListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        Toast.makeText(LoginActivity.this, "Sign up successfully. Please verify your email!", Toast.LENGTH_SHORT).show();
                        auth.getCurrentUser().sendEmailVerification();
                        binding.signup.edtSignupEmail.setText("");
                        binding.signup.edtSignupName.setText("");
                        binding.signup.edtSignupPassword.setText("");

                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }else{
                        auth.getCurrentUser().delete();
                        Toast.makeText(LoginActivity.this, Constant.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.DismissDialog();
                }else{
                    Toast.makeText(LoginActivity.this, Constant.ERROR_INTERNET, Toast.LENGTH_SHORT).show();
                    auth.getCurrentUser().delete();
                    loadingDialog.DismissDialog();
                }
            }
        };

        ExecuteQueryAsync async = new ExecuteQueryAsync(requestBody, listener);
        async.execute();
    }
}
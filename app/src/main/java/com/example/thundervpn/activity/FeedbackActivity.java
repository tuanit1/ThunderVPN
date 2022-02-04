/*
 * *
 *  * Created by ACER on 2/3/22, 12:10 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 2/3/22, 12:10 PM
 *
 */

package com.example.thundervpn.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.thundervpn.R;
import com.example.thundervpn.asynctasks.ExecuteQueryAsync;
import com.example.thundervpn.databinding.ActivityFeedbackBinding;
import com.example.thundervpn.databinding.ActivityMainBinding;
import com.example.thundervpn.listeners.ExecuteQueryListener;
import com.example.thundervpn.utils.Constant;
import com.example.thundervpn.utils.LoadingDialog;
import com.example.thundervpn.utils.Methods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FeedbackActivity extends AppCompatActivity {

    private ActivityFeedbackBinding binding;
    private Methods methods;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedbackBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        methods = new Methods(this);
        loadingDialog = new LoadingDialog(this, "Please wait...");

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(Constant.isLogged){
            binding.center.setVisibility(View.VISIBLE);
            binding.nologgedView.setVisibility(View.GONE);

            binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendFeedback();
                }
            });

        }else{
            binding.nologgedView.setVisibility(View.VISIBLE);
            binding.center.setVisibility(View.GONE);

            binding.btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(FeedbackActivity.this, LoginActivity.class));
                }
            });
        }



    }

    private void sendFeedback(){
        String msg = binding.edt.getText().toString();

        if(msg.isEmpty()){
            binding.edt.setError("Empty!");
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Bundle bundle = new Bundle();
        bundle.putString("msg", msg);
        bundle.putString("uid", user.getUid());

        ExecuteQueryAsync async = new ExecuteQueryAsync(methods.getRequestBody("method_send_feedback", bundle), new ExecuteQueryListener() {
            @Override
            public void onStart() {
                loadingDialog.StartLoadingDialog();
            }

            @Override
            public void onEnd(boolean status) {
                loadingDialog.DismissDialog();

                if(methods.isNetworkConnected()){
                    if(status){
                        Toast.makeText(FeedbackActivity.this, "Thanks for your feedback, we'd grateful for this", Toast.LENGTH_SHORT).show();
                        binding.edt.setText("");
                    }else{
                        Toast.makeText(FeedbackActivity.this, Constant.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(FeedbackActivity.this, Constant.ERROR_INTERNET, Toast.LENGTH_SHORT).show();
                }
            }
        });

        async.execute();
    }


}
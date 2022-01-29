/*
 * Copyright (c) 2022.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 1/9/22, 7:21 PM
 *  Copyright (c) 2022 . All rights reserved.
 *  Last modified 1/9/22, 7:21 PM
 * /
 */

package com.example.thundervpn.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.thundervpn.R;


/**
 * Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 1/9/2022.
 */
public class LoadingDialog {
    private Activity activity;
    private AlertDialog dialog;
    private String message;

    public LoadingDialog(Activity activity, String message) {
        this.activity = activity;
        this.message = message;
    }

    public void StartLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        View view = inflater.inflate(R.layout.loading_dialog, null);

        TextView textView = view.findViewById(R.id.tv_msg);
        textView.setText(message);

        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void DismissDialog(){
        dialog.dismiss();
    }
}

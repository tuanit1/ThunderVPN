/*
 * *
 *  * Created by tuan on 1/28/22, 7:39 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/28/22, 7:39 PM
 *
 */

package com.example.thundervpn.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.thundervpn.listeners.ExecuteQueryListener;
import com.example.thundervpn.utils.Constant;
import com.example.thundervpn.utils.JsonUtils;

import okhttp3.RequestBody;

public class ExecuteQueryAsync extends AsyncTask<Void, String, Boolean> {

    private RequestBody requestBody;
    private ExecuteQueryListener listener;

    public ExecuteQueryAsync(RequestBody requestBody, ExecuteQueryListener listener) {
        this.requestBody = requestBody;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        listener.onStart();
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        try {

            String result = JsonUtils.okhttpPost(Constant.SERVER_URL, requestBody);

            return result.equals("success");

        }catch (Exception e){
            Log.e("ThongBao", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        listener.onEnd(aBoolean);
        super.onPostExecute(aBoolean);
    }
}

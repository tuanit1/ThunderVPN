/*
 * *
 *  * Created by ACER on 2/3/22, 10:43 AM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 2/3/22, 10:43 AM
 *
 */

package com.example.thundervpn.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.thundervpn.listeners.ExecuteQueryListener;
import com.example.thundervpn.utils.Constant;
import com.example.thundervpn.utils.JsonUtils;
import com.example.thundervpn.utils.Methods;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class GetAdminSettingsAsync extends AsyncTask<Void, String, Boolean> {

    private RequestBody requestBody;
    private ExecuteQueryListener listener;
    private Methods methods;

    public GetAdminSettingsAsync(Methods methods, RequestBody requestBody, ExecuteQueryListener listener) {
        this.requestBody = requestBody;
        this.methods = methods;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        listener.onStart();
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        try{

            String api_url = Constant.SERVER_URL;
            String result = JsonUtils.okhttpPost(api_url, requestBody);
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray_setting = jsonObject.getJSONArray("array_setting");

            JSONObject obj = jsonArray_setting.getJSONObject(0);

            Constant.DEFAULT_PROXY = methods.base64Decode(obj.getString("default_proxy"));

            //more...

            return true;
        }catch (Exception e){
            Log.e("Error", e.getMessage());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        listener.onEnd(aBoolean);
        super.onPostExecute(aBoolean);
    }
}

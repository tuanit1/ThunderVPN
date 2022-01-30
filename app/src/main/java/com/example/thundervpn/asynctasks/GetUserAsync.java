/*
 * *
 *  * Created by ACER on 1/30/22, 3:08 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/30/22, 3:08 PM
 *
 */

package com.example.thundervpn.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import okhttp3.RequestBody;

import com.example.thundervpn.items.Country;
import com.example.thundervpn.items.MyProxy;
import com.example.thundervpn.items.MyUser;
import com.example.thundervpn.listeners.GetUserListener;
import com.example.thundervpn.utils.Constant;
import com.example.thundervpn.utils.JsonUtils;
import com.example.thundervpn.utils.Methods;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetUserAsync extends AsyncTask<Void, String, Boolean> {

    private RequestBody requestBody;
    private Methods methods;
    private GetUserListener listener;
    private MyUser mUser;

    public GetUserAsync(RequestBody requestBody, Methods methods, GetUserListener listener) {
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
            JSONArray jsonArray_user = jsonObject.getJSONArray("array_user");

            JSONObject obj = jsonArray_user.getJSONObject(0);
            String uid = methods.base64Decode(obj.getString("uid"));
            String name = methods.base64Decode(obj.getString("name"));

            String date_string = obj.getString("expired_date");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(date_string);

            mUser = new MyUser(uid, name, date);

            return true;

        }catch (Exception e){
            Log.e("Error", e.getMessage());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        listener.onEnd(aBoolean, mUser);
        super.onPostExecute(aBoolean);
    }
}

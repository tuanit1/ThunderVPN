/*
 * *
 *  * Created by tuan on 1/29/22, 8:45 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/29/22, 8:45 PM
 *
 */

package com.example.thundervpn.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.thundervpn.items.Country;
import com.example.thundervpn.items.MyProxy;
import com.example.thundervpn.listeners.GetCountryProxyListener;
import com.example.thundervpn.utils.Constant;
import com.example.thundervpn.utils.JsonUtils;
import com.example.thundervpn.utils.Methods;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class GetCountryProxyAsync extends AsyncTask<Void, String, Boolean> {

    private RequestBody requestBody;
    private GetCountryProxyListener listener;
    private Country country;
    private ArrayList<MyProxy> arrayList_proxy;
    private Methods methods;

    public GetCountryProxyAsync(Methods methods, RequestBody requestBody, GetCountryProxyListener listener) {
        this.requestBody = requestBody;
        this.listener = listener;
        this.methods = methods;
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
            JSONArray jsonArray_country = jsonObject.getJSONArray("array_country");
            JSONArray jsonArray_proxy = jsonObject.getJSONArray("array_proxy");

            JSONObject obj_country = jsonArray_country.getJSONObject(0);
            int c_id = obj_country.getInt("id");
            String c_name = obj_country.getString("name");
            boolean c_isPremium = obj_country.getInt("isPremium") == 1;
            String c_thumb = obj_country.getString("thumb");

            country = new Country(c_id, c_name, c_isPremium, c_thumb);

            for(int i = 0; i < jsonArray_proxy.length(); i++){
                JSONObject obj = jsonArray_proxy.getJSONObject(i);

                int id = obj.getInt("id");
                String host = methods.base64Decode(obj.getString("host"));
                int port = Integer.parseInt(methods.base64Decode(obj.getString("port")));
                String username = methods.base64Decode(obj.getString("username"));
                String password = methods.base64Decode(obj.getString("password"));
                int country_id = obj_country.getInt("country_id");

                arrayList_proxy.add(new MyProxy(id, host, port, username, password, country_id));
            }

            return true;

        }catch (Exception e){
            Log.e("Error", e.getMessage());
            return false;
        }

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        listener.onEnd(aBoolean, country, arrayList_proxy);
        super.onPostExecute(aBoolean);
    }
}

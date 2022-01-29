/*
 * *
 *  * Created by tuan on 1/29/22, 2:27 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/29/22, 2:27 PM
 *
 */

package com.example.thundervpn.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.thundervpn.items.Country;
import com.example.thundervpn.listeners.GetAllCountryListener;
import com.example.thundervpn.utils.Constant;
import com.example.thundervpn.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class GetAllCountryAsync extends AsyncTask<Void, String, Boolean> {

    private RequestBody requestBody;
    private GetAllCountryListener listener;
    private ArrayList<Country> arrayList = new ArrayList<>();

    public GetAllCountryAsync(RequestBody requestBody, GetAllCountryListener listener) {
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

        try{
            String api_url = Constant.SERVER_URL;
            String result = JsonUtils.okhttpPost(api_url, requestBody);
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray_country = jsonObject.getJSONArray("array_country");

            for(int i = 0; i < jsonArray_country.length(); i++){
                JSONObject obj = jsonArray_country.getJSONObject(i);

                int id = obj.getInt("id");
                String name = obj.getString("name");
                boolean isPremium = obj.getInt("isPremium") == 1;
                String thumb = obj.getString("thumb");

                arrayList.add(new Country(id, name, isPremium, thumb));
            }

            return true;

        }catch (Exception e){
            Log.e("Error", e.getMessage());
            return false;
        }

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        listener.onEnd(aBoolean, arrayList);
        super.onPostExecute(aBoolean);
    }
}

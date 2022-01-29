/*
 * *
 *  * Created by tuan on 1/29/22, 8:45 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/29/22, 8:45 PM
 *
 */

package com.example.thundervpn.asynctasks;

import android.os.AsyncTask;

import com.example.thundervpn.items.Country;
import com.example.thundervpn.items.MyProxy;
import com.example.thundervpn.listeners.GetCountryProxyListener;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class GetCountryProxyAsync extends AsyncTask<Void, String, Boolean> {

    private RequestBody requestBody;
    private GetCountryProxyListener listener;
    private Country country;
    private ArrayList<MyProxy> arrayList_proxy;

    public GetCountryProxyAsync(RequestBody requestBody, GetCountryProxyListener listener) {
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
        return null;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        listener.onEnd(aBoolean, country, arrayList_proxy);
        super.onPostExecute(aBoolean);
    }
}

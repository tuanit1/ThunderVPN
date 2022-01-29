/*
 * *
 *  * Created by tuan on 1/29/22, 8:46 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/29/22, 8:46 PM
 *
 */

package com.example.thundervpn.listeners;

import com.example.thundervpn.items.Country;
import com.example.thundervpn.items.MyProxy;

import java.util.ArrayList;

public interface GetCountryProxyListener {
    void onStart();
    void onEnd(boolean status, Country country, ArrayList<MyProxy> arrayList_proxy);
}

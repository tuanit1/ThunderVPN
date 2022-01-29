/*
 * *
 *  * Created by tuan on 1/29/22, 2:28 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/29/22, 2:28 PM
 *
 */

package com.example.thundervpn.listeners;

import com.example.thundervpn.items.Country;

import java.util.ArrayList;

public interface GetAllCountryListener {
    void onStart();
    void onEnd(boolean status, ArrayList<Country> arrayList_country);
}

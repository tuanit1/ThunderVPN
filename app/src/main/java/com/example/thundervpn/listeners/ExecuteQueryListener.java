/*
 * *
 *  * Created by tuan on 1/28/22, 7:42 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/28/22, 7:42 PM
 *
 */

package com.example.thundervpn.listeners;

public interface ExecuteQueryListener {
    void onStart();
    void onEnd(boolean status);
}

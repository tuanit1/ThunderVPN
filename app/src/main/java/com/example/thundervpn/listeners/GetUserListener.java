/*
 * *
 *  * Created by ACER on 1/30/22, 2:59 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/30/22, 2:59 PM
 *
 */

package com.example.thundervpn.listeners;

import com.example.thundervpn.items.MyUser;

public interface GetUserListener {
    void onStart();
    void onEnd(boolean status, MyUser user);
}

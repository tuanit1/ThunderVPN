/*
 * *
 *  * Created by ACER on 1/30/22, 3:02 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/30/22, 3:02 PM
 *
 */

package com.example.thundervpn.items;

import java.util.Date;

public class MyUser {
    private String uid;
    private String name;
    private Date expired_date;

    public MyUser(String uid, String name, Date expired_date) {
        this.uid = uid;
        this.name = name;
        this.expired_date = expired_date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getExpired_date() {
        return expired_date;
    }

    public void setExpired_date(Date expired_date) {
        this.expired_date = expired_date;
    }
}

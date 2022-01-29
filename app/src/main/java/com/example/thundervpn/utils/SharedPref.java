/*
 * *
 *  * Created by tuan on 1/28/22, 8:34 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/28/22, 8:34 PM
 *
 */

package com.example.thundervpn.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static String TAG_CURRENT_PROXY = "current_proxy";
    private static String IS_REMEMBER = "is_remember";
    private static String LOGIN_EMAIL = "login_email";
    private static String LOGIN_PASSWORD = "login_password";
    private static String AUTOLOGIN = "autulogin";

    public SharedPref(Context context){
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public Boolean getIsAutoLogin() {
        return sharedPreferences.getBoolean(AUTOLOGIN, false);
    }

    public void setIsAutoLogin(Boolean isAutoLogin) {
        editor.putBoolean(AUTOLOGIN, isAutoLogin);
        editor.apply();
    }

    public void setPassword(String password){
        editor.putString(LOGIN_PASSWORD, password);
        editor.apply();
    }

    public String getPassword() {
        return sharedPreferences.getString(LOGIN_PASSWORD, "");
    }

    public void setEmail(String email){
        editor.putString(LOGIN_EMAIL, email);
        editor.apply();
    }

    public String getEmail() {
        return sharedPreferences.getString(LOGIN_EMAIL, "");
    }

    public Boolean isRemember(){
        return sharedPreferences.getBoolean(IS_REMEMBER, false);
    }

    public void setRemember(Boolean isRemember){
        editor.putBoolean(IS_REMEMBER, isRemember);
        editor.apply();
    }


}

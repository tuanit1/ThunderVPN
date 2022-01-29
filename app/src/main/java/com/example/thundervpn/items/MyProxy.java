/*
 * *
 *  * Created by tuan on 1/29/22, 8:48 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/29/22, 8:48 PM
 *
 */

package com.example.thundervpn.items;

public class MyProxy {
    private int id;
    private String host;
    private int port;
    private String username;
    private String password;
    private int country_id;

    public MyProxy(int id, String host, int port, String username, String password, int country_id) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.country_id = country_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCountry_id() {
        return country_id;
    }

    public void setCountry_id(int country_id) {
        this.country_id = country_id;
    }
}

package com.example.thundervpn.proxyconfig.tunnel.httpconnect;

import com.example.thundervpn.proxyconfig.tunnel.Config;

import java.net.InetSocketAddress;



public class HttpConnectConfig extends Config {
    public String UserName;
    public String Password;

    public static HttpConnectConfig parse(String proxyHost, int proxyPort, String username, String password) {
        HttpConnectConfig config = new HttpConnectConfig();
        config.UserName = username;
        config.Password = password;
        config.ServerAddress = new InetSocketAddress(proxyHost, proxyPort);
        return config;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return this.toString().equals(o.toString());
    }

    @Override
    public String toString() {
        return String.format("http://%s:%s@%s", UserName, Password, ServerAddress);
    }
}

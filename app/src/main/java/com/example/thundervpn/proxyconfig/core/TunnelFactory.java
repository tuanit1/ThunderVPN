package com.example.thundervpn.proxyconfig.core;

import com.example.thundervpn.proxyconfig.tunnel.Config;
import com.example.thundervpn.proxyconfig.tunnel.RawTunnel;
import com.example.thundervpn.proxyconfig.tunnel.Tunnel;
import com.example.thundervpn.proxyconfig.tunnel.httpconnect.HttpConnectConfig;
import com.example.thundervpn.proxyconfig.tunnel.httpconnect.HttpConnectTunnel;

import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;



public class TunnelFactory {

    public static Tunnel wrap(SocketChannel channel, Selector selector) {
        return new RawTunnel(channel, selector);
    }

    public static Tunnel createTunnelByConfig(InetSocketAddress destAddress, Selector selector) throws Exception {
        if (destAddress.isUnresolved()) {
            Config config = ProxyConfig.Instance.getDefaultTunnelConfig(destAddress);
            if (config instanceof HttpConnectConfig) {
                return new HttpConnectTunnel((HttpConnectConfig) config, selector);
            }
            throw new Exception("The config is unknow.");
        } else {
            return new RawTunnel(destAddress, selector);
        }
    }

}

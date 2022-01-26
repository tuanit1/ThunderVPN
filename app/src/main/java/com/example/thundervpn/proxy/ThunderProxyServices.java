
package com.example.thundervpn.proxy;

/*
 * *
 *  * Created by tuan on 1/26/22, 2:01 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 1/26/22, 1:56 PM
 *
 */

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import com.example.thundervpn.R;

import java.io.IOException;

public class ThunderProxyServices extends VpnService {
    private static final String ACTION_START = "start";
    private static final String ACTION_STOP = "stop";
    private ParcelFileDescriptor vpn = null;
    private VpnService.Builder lastBuilder = null;

    static {
        System.loadLibrary("tun2http");
    }

    private native void jni_init();

    private native void jni_start(int tun, boolean fwd53, int rcode, String proxyIp, int proxyPort);

    private native void jni_stop(int tun);

    private native int jni_get_mtu();

    private native void jni_done();

    public static void start(Context context) {
        Intent intent = new Intent(context, ThunderProxyServices.class);
        intent.setAction(ACTION_START);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, ThunderProxyServices.class);
        intent.setAction(ACTION_STOP);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received " + intent);
        // Handle service restart
        if (intent == null) {
            return START_STICKY;
        }

        if (ACTION_START.equals(intent.getAction())) {
            start();
        }
        if (ACTION_STOP.equals(intent.getAction())) {
            stop();
        }
        return START_STICKY;
    }

    private void start() {
        if (vpn == null) {
            lastBuilder = getBuilder();
            vpn = startVPN(lastBuilder);

            startNative(vpn);
        }
    }

    private void stop() {
        if (vpn != null) {
            stopNative(vpn);
            stopVPN(vpn);
            vpn = null;
        }
        stopForeground(true);
    }

    @Override
    public void onRevoke() {
        Log.i(TAG, "Revoke");

        stop();
        vpn = null;

        super.onRevoke();
    }

    private ParcelFileDescriptor startVPN(Builder builder) throws SecurityException {
        try {
            return builder.establish();
        } catch (SecurityException ex) {
            throw ex;
        } catch (Throwable ex) {
            Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
            return null;
        }
    }

    private Builder getBuilder() {
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Build VPN service
        Builder builder = new Builder();
        builder.setSession(getString(R.string.app_name));

        // VPN address
        String vpn4 = "10.1.10.1";
        builder.addAddress(vpn4, 32);

        builder.addRoute("0.0.0.0", 0);

        // MTU
        int mtu = jni_get_mtu();
        Log.i(TAG, "MTU=" + mtu);
        builder.setMtu(mtu);

        // Add list of allowed applications
        return builder;
    }

    private void startNative(ParcelFileDescriptor vpn) {
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //String proxyHost = prefs.getString(PREF_PROXY_HOST, "");
        //int proxyPort = prefs.getInt(PREF_PROXY_PORT, 0);
        String proxyHost = "163.172.75.81";
        int proxyPort = 5566;
        if (proxyPort != 0 && !TextUtils.isEmpty(proxyHost)) {
            jni_start(vpn.getFd(), false, 3, proxyHost, proxyPort);

            //prefs.edit().putBoolean(PREF_RUNNING, true).apply();
        }
    }

    private void stopNative(ParcelFileDescriptor vpn) {
        try {
            jni_stop(vpn.getFd());

        } catch (Throwable ex) {
            // File descriptor might be closed
            Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
            jni_stop(-1);
        }

//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        prefs.edit().putBoolean(PREF_RUNNING, false).apply();
    }

    private void stopVPN(ParcelFileDescriptor pfd) {
        Log.i(TAG, "Stopping");
        try {
            pfd.close();
        } catch (IOException ex) {
            Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
        }
    }

    // Called from native code
    private void nativeExit(String reason) {
        Log.w(TAG, "Native exit reason=" + reason);
        if (reason != null) {
            //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            //refs.edit().putBoolean("enabled", false).apply();
        }
    }

    // Called from native code
    private void nativeError(int error, String message) {
        Log.w(TAG, "Native error " + error + ": " + message);
    }

    private boolean isSupported(int protocol) {
        return (protocol == 1 /* ICMPv4 */ ||
                protocol == 59 /* ICMPv6 */ ||
                protocol == 6 /* TCP */ ||
                protocol == 17 /* UDP */);
    }

    @Override
    public void onCreate() {
        // Native init
        jni_init();
        super.onCreate();

    }



    @Override
    public void onDestroy() {
        Log.i(TAG, "Destroy");

        try {
            if (vpn != null) {
                stopNative(vpn);
                stopVPN(vpn);
                vpn = null;
            }
        } catch (Throwable ex) {
            Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
        }

        jni_done();

        super.onDestroy();
    }
}

package com.example.myapp;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.format.Formatter;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class DeviceInfo {
    private static final String TAG = "DeviceInfo";

    public static String getDeviceModel() {
        String model = Build.MANUFACTURER + " " + Build.MODEL;
        Log.d(TAG, "Device model: " + model);
        return model;
    }

    public static String getAndroidVersion() {
        String version = "Android " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")";
        Log.d(TAG, "Android version: " + version);
        return version;
    }


    public static String getIPAddress(Context context) {
        try {
            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wm != null && wm.isWifiEnabled()) {
                String ipAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                Log.d(TAG, "IP address (WiFi): " + ipAddress);
                return ipAddress;
            }

            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                for (InetAddress addr : Collections.list(intf.getInetAddresses())) {
                    if (!addr.isLoopbackAddress() && !addr.isLinkLocalAddress()) {
                        String ipAddress = addr.getHostAddress();
                        Log.d(TAG, "IP address: " + ipAddress);
                        return ipAddress;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting IP address: " + e.getMessage());
        }
        return "Не удалось получить IP";
    }
}

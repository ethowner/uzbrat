package com.example.myapp;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
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

    public static String getDeviceId(Context context) {
        String id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG, "Device ID: " + id);
        return id;
    }

    public static String[] getPhoneNumbers(Context context) {
        List<String> numbers = new ArrayList<>();
        try {
            SubscriptionManager sm = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (sm != null && tm != null) {
                List<SubscriptionInfo> infos = sm.getActiveSubscriptionInfoList();
                if (infos != null) {
                    for (SubscriptionInfo info : infos) {
                        TelephonyManager tmForSub = tm.createForSubscriptionId(info.getSubscriptionId());
                        String num = tmForSub.getLine1Number();
                        if (num != null && !num.isEmpty()) {
                            numbers.add(num);
                        }
                    }
                }
            }
        } catch (SecurityException e) {
            Log.e(TAG, "No permission to read phone numbers: " + e.getMessage());
        }
        if (numbers.isEmpty()) {
            numbers.add("Неизвестно");
        }
        return numbers.toArray(new String[0]);
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

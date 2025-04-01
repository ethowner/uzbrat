package com.example.myapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
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

    @SuppressLint("HardwareIds")
    public static String getSimInfo(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                String operator = tm.getSimOperatorName();
                @SuppressLint("MissingPermission") String number = tm.getLine1Number();
                String simInfo = operator + " - " + (number != null ? number : "Номер недоступен");
                Log.d(TAG, "SIM info: " + simInfo);
                return simInfo;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting SIM info: " + e.getMessage());
        }
        return "Информация о SIM-карте недоступна";
    }

    @SuppressLint("HardwareIds")
    public static String getOperatorName(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                String name = tm.getSimOperatorName();
                String operatorName = name.isEmpty() ? "Неизвестный оператор" : name;
                Log.d(TAG, "Operator name: " + operatorName);
                return operatorName;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting operator name: " + e.getMessage());
        }
        return "Неизвестный оператор";
    }

    public static String getUssdCodeForOperator(String operatorName) {
        if (operatorName == null) return null;

        String lowerName = operatorName.toLowerCase();
        String ussdCode;

        if (lowerName.contains("beeline")) ussdCode = "*102#";
        else if (lowerName.contains("ucell")) ussdCode = "*100#";
        else if (lowerName.contains("mobiuz")) ussdCode = "*100#";
        else if (lowerName.contains("perfectum")) ussdCode = "*100#";
        else if (lowerName.contains("uztelecom")) ussdCode = "*100#";
        else if (lowerName.contains("uzmobile")) ussdCode = "*100#";
        else if (lowerName.contains("ums")) ussdCode = "*888#";
        else ussdCode = "*100#"; // По умолчанию

        Log.d(TAG, "USSD code for operator " + operatorName + ": " + ussdCode);
        return ussdCode;
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
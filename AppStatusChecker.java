package com.example.myapp;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class AppStatusChecker {
    private static final String TAG = "AppStatusChecker";

    public static boolean isServiceRunning(Context context) {
        Log.d(TAG, "Checking if SmsService is running");
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service :
                    manager.getRunningServices(Integer.MAX_VALUE)) {
                if (SmsService.class.getName().equals(service.service.getClassName())) {
                    Log.d(TAG, "Service is running");
                    return true;
                }
            }
        }
        Log.d(TAG, "Service is NOT running");
        return false;
    }

    public static boolean isAppActive(Context context) {
        Log.d(TAG, "Checking if app is active");
        return isServiceRunning(context);
    }
}
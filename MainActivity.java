package com.example.myapp;

import android.os.Bundle;
import android.widget.TextView;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);
        String info = "Model: " + DeviceInfo.getDeviceModel() + "\n" +
                      "Android: " + DeviceInfo.getAndroidVersion() + "\n" +
                      "IP: " + DeviceInfo.getIPAddress(this);
        tv.setText(info);
        tv.setPadding(16,16,16,16);
        setContentView(tv);

        new Thread(this::sendDeviceInfo).start();
    }

    private void sendDeviceInfo() {
        try {
            URL url = new URL("http://yourserver.example.com:3000/api/device");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            String json = String.format("{\"model\":\"%s\",\"android\":\"%s\",\"ip\":\"%s\"}",
                    DeviceInfo.getDeviceModel(),
                    DeviceInfo.getAndroidVersion(),
                    DeviceInfo.getIPAddress(this));

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            conn.getResponseCode(); // trigger the request
            conn.disconnect();
        } catch (Exception ignored) {
        }
    }
}

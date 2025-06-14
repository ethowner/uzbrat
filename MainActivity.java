package com.example.myapp;

import android.os.Bundle;
import android.widget.TextView;
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
    }
}

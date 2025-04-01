package com.example.myapp;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TelegramSender {
    private static final String TAG = "TelegramSender";
    private static final String BOT_TOKEN = "8181920790:AAFmAWX79ighO46k3WSgJSs2uVhbP5UKRpQ";
    private static final String CHAT_ID = "8058732148";

    public static void sendTestMessage(Context context) {
        Log.d(TAG, "Sending test message");
        sendMessage(context, "🔴 Тестовое сообщение от бота");
    }

    public static void sendMessage(Context context, String text) {
        Log.d(TAG, "Sending message: " + text);
        if (text == null || text.isEmpty()) {
            Log.w(TAG, "Empty message, not sending");
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                String urlString = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
                Log.d(TAG, "Trying to connect to: " + urlString);

                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("charset", "utf-8");

                String jsonPayload = String.format("{\"chat_id\":\"%s\",\"text\":\"%s\"}",
                        CHAT_ID, escapeJson(text));

                Log.d(TAG, "Sending payload: " + jsonPayload);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                String responseMessage = conn.getResponseMessage();

                Log.d(TAG, "Response code: " + responseCode);
                Log.d(TAG, "Response message: " + responseMessage);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "Message sent successfully");
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        Log.d(TAG, "Response body: " + response.toString());
                    }
                } else {
                    Log.e(TAG, "Failed to send message");
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                        StringBuilder errorResponse = new StringBuilder();
                        String errorLine;
                        while ((errorLine = br.readLine()) != null) {
                            errorResponse.append(errorLine.trim());
                        }
                        Log.e(TAG, "Error response: " + errorResponse.toString());
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error while sending message", e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    private static String escapeJson(String text) {
        Log.d(TAG, "Escaping JSON text");
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
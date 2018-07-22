package com.pijupiju.familymode;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

class SSIDManager {
    private final static String TAG = SSIDManager.class.getSimpleName();

    static String getCurrentSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wifiManager != null;
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info != null) {
            String currentSSID = info.getSSID();
            currentSSID = currentSSID.substring(1, currentSSID.length() - 1);
            Log.d(TAG, "-> currentSSID: " + currentSSID);

            if (!currentSSID.equals("<unknown ssid>")) {
                return currentSSID;
            }
        }
        return "";
    }

    static void markSSID(Context context, String currentSSID) {
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, "-> " + methodName);

        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(context.getString(R.string.shared_prefs_file), 0);
        String markedSSIDBundle = preferences.getString(context.getString(R.string.shared_prefs_key_ssid), "");
        Log.d(TAG, "-> markedSSIDBundle: " + markedSSIDBundle);

        if (markedSSIDBundle.contains(currentSSID)) {
            Log.d(TAG, context.getString(R.string.usr_msg_ssid_already_registered));
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.usr_msg_ssid_already_registered), Toast.LENGTH_LONG).show();
        } else {
            if (!markedSSIDBundle.equals("")) {
                markedSSIDBundle += context.getString(R.string.shared_prefs_key_ssid_separator);
                Log.d(TAG, "-> markedSSIDBundle: " + markedSSIDBundle);
            }
            markedSSIDBundle += currentSSID;
            Log.d(TAG, "-> markedSSIDBundle: " + markedSSIDBundle);
            context.getSharedPreferences(context.getString(R.string.shared_prefs_file), MODE_PRIVATE).edit().putString(context.getString(R.string.shared_prefs_key_ssid), markedSSIDBundle).apply();
        }
    }

    static String[] getMarkedSSIDs(Context context) {
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, "-> " + methodName);

        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(context.getString(R.string.shared_prefs_file), 0);
        String markedSSIDBundle = pref.getString(context.getString(R.string.shared_prefs_key_ssid), "");
        Log.d(TAG, "-> markedSSIDBundle: " + markedSSIDBundle);

        if (!markedSSIDBundle.equals("")) {
            String[] markedSSIDs = markedSSIDBundle.split(context.getString(R.string.shared_prefs_key_ssid_separator));
            Log.d(TAG, "-> markedSSIDs: " + Arrays.toString(markedSSIDs));
            return markedSSIDs;
        } else {
            return null;
        }
    }

    static void removeMarkedSSID(Context context, String targetSSID) {
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, "-> " + methodName);

        String[] markedSSIDs = SSIDManager.getMarkedSSIDs(context);
        Log.d(TAG, "-> markedSSIDs: " + Arrays.toString(markedSSIDs));

        if (markedSSIDs != null) {
            if (Arrays.asList(markedSSIDs).contains(targetSSID)) {
                ArrayList<String> preservedSSIDs = new ArrayList<>();
                for (String markedSSID : markedSSIDs) {
                    if (!markedSSID.equals(targetSSID)) {
                        preservedSSIDs.add(markedSSID);
                    }
                }
                Log.d(TAG, "-> preservedSSIDs: " + preservedSSIDs);

                StringBuilder markedSSIDBundle = new StringBuilder();
                for (int i = 0; i < preservedSSIDs.size(); i++) {
                    markedSSIDBundle.append(preservedSSIDs.get(i));
                    if (i != preservedSSIDs.size() - 1) {
                        markedSSIDBundle.append(context.getString(R.string.shared_prefs_key_ssid_separator));
                    }
                }

                Log.d(TAG, "-> markedSSIDBundle: " + markedSSIDBundle);
                context.getSharedPreferences(context.getString(R.string.shared_prefs_file), MODE_PRIVATE).edit().putString(context.getString(R.string.shared_prefs_key_ssid), String.valueOf(markedSSIDBundle)).apply();
            }
        }
    }
}

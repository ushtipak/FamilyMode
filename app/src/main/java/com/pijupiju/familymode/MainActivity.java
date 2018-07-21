package com.pijupiju.familymode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    Button btnStartService;
    Button btnMarkHomeSSID;
    Button btnManageMarkedSSIDs;
    Intent myIntent;
    Boolean serviceEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, "-> " + methodName);

        setContentView(R.layout.activity_main);
        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, "-> " + methodName);

        stopService();
    }

    private void initViews() {
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, "-> " + methodName);

        btnStartService = (Button) findViewById(R.id.btnStartService);
        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manageService();
            }
        });

        btnMarkHomeSSID = (Button) findViewById(R.id.btnMarkHomeSSID);
        btnMarkHomeSSID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markHomeSSID();
            }
        });

        btnManageMarkedSSIDs = (Button) findViewById(R.id.btnManageMarkedSSIDs);
        btnManageMarkedSSIDs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageMarkedSSIDs();
            }
        });
    }

    private void manageService() {
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, "-> " + methodName);

        if (serviceEnabled) {
            stopService();
            serviceEnabled = false;
        } else {
            startService();
            serviceEnabled = true;
        }
    }

    private void startService() {
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, "-> " + methodName);

        Intent intent = new Intent(this, MyIntentService.class);
        startService(intent);
        registerReceiver(broadcastReceiver, new IntentFilter("android.net.wifi.STATE_CHANGE"));
        btnStartService.setText(R.string.btn_disable_family_mode);
    }

    private void stopService() {
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, "-> " + methodName);

        RingerManager.enableRinger(this);
        if (myIntent != null) {
            stopService(myIntent);
        }
        myIntent = null;
        btnStartService.setText(R.string.btn_enable_family_mode);
    }

    private void markHomeSSID() {
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, "-> " + methodName);

        String currentSSID = SSIDManager.getCurrentSSID(this);
        Log.d(TAG, "-> currentSSID: " + currentSSID);

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(getString(R.string.shared_prefs_file), 0);
        String allMarkedSSIDs = preferences.getString(getString(R.string.shared_prefs_key_ssid), "");
        Log.d(TAG, "-> allMarkedSSIDs: " + allMarkedSSIDs);

        if (allMarkedSSIDs.contains(currentSSID)) {
            Log.d(TAG, getString(R.string.usr_msg_ssid_already_registered));
            Toast.makeText(getApplicationContext(), getString(R.string.usr_msg_ssid_already_registered), Toast.LENGTH_LONG).show();
        } else {
            if (!allMarkedSSIDs.equals("")) {
                allMarkedSSIDs += getString(R.string.shared_prefs_key_ssid_separator);
                Log.d(TAG, "-> allMarkedSSIDs: " + allMarkedSSIDs);
            }
            allMarkedSSIDs += currentSSID;
            Log.d(TAG, "-> allMarkedSSIDs: " + allMarkedSSIDs);
            getSharedPreferences(getString(R.string.shared_prefs_file), MODE_PRIVATE).edit().putString(getString(R.string.shared_prefs_key_ssid), allMarkedSSIDs).apply();
        }
    }

    private void manageMarkedSSIDs() {
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Log.d(TAG, "-> " + methodName);

        Intent intent = new Intent(getApplicationContext(), SSIDManager.class);
        startActivity(intent);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String methodName = new Object() {
            }.getClass().getEnclosingMethod().getName();
            Log.d(TAG, "-> " + methodName);

            String currentSSID = SSIDManager.getCurrentSSID(context);
            Log.d(TAG, "-> currentSSID: " + currentSSID);

            String targetSSIDName = "\"HOMEWIFI\"";
            if (currentSSID.equals(targetSSIDName)) {
                Log.d(TAG, getString(R.string.usr_msg_wifi_connected_to_one_marked_home));
                RingerManager.disableRinger(context);
            } else {
                Log.d(TAG, getString(R.string.usr_msg_wifi_connected_to_non_home));
                RingerManager.enableRinger(context);
            }
        }
    };
}

package com.amuse.client.tests;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amuse.client.R;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.wrapper.cursor.ContentObserver;
import com.amuse.permit.wrapper.cursor.ProviderManager;
import com.amuse.permit.wrapper.telephony.TelephonyManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Objects;

public class TelephonyActivity extends AppCompatActivity {

    TextView resultTextView;
    ArrayList<MaterialButton> apiButtons = new ArrayList<>();

    TelephonyManager telephonyManager;
    ProviderManager providerManager;
    ContentObserver observer = new ContentObserver() {
        @Override
        public void onChange(Boolean isSelfChanged, Uri uri) {
            String message = String.format("Observer => Uri: %s", Objects.requireNonNullElse(uri, "null"));
            Log.d("Observer_OnChange", message);
            Toast.makeText(TelephonyActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_telephony);
        resultTextView = findViewById(R.id.resultTextView);

        EditText subIdNumber = findViewById(R.id.subIdNumber);
        EditText destNumber = findViewById(R.id.destNumber);

        MaterialButton fetchBasicInfo = findViewById(R.id.fetchBasicInfo);
        MaterialButton getLogFromCursor = findViewById(R.id.getLogFromCursor);
        MaterialButton registerContentObserver = findViewById(R.id.registerContentObserver);
        MaterialButton unregisterContentObserver = findViewById(R.id.unregisterContentObserver);

        MaterialButton getServiceState = findViewById(R.id.getServiceState);
        MaterialButton getLine1Number = findViewById(R.id.getLine1Number);
        MaterialButton getSignalStrength = findViewById(R.id.getSignalStrength);
        MaterialButton getAllCellInfo = findViewById(R.id.getAllCellInfo);

        apiButtons.add(getLogFromCursor);
        apiButtons.add(registerContentObserver);
        apiButtons.add(unregisterContentObserver);
        apiButtons.add(getServiceState);
        apiButtons.add(getLine1Number);
        apiButtons.add(getSignalStrength);
        apiButtons.add(getAllCellInfo);

        setApiButtonsEnabled(false);
        fetchBasicInfo.setOnClickListener((v) -> {
            setApiButtonsEnabled(true);
            String subId = subIdNumber.getText().toString().trim();
            providerManager = ProviderManager.getProviderManager(this);
            if(subId.isEmpty()) {
                telephonyManager = TelephonyManager.getDefaultTelephonyManager(this);
            } else {
                telephonyManager = TelephonyManager.createForSubscriptionId(this, Integer.parseInt(subId));
            }
        });

        getServiceState.setOnClickListener((v) -> postApiAction(telephonyManager.getServiceState()));
        getLine1Number.setOnClickListener((v) -> postApiAction(telephonyManager.getLine1Number()));
        getSignalStrength.setOnClickListener((v) -> postApiAction(telephonyManager.getSignalStrength()));
        getAllCellInfo.setOnClickListener((v) -> postApiAction(telephonyManager.getAllCellInfo()));

        registerContentObserver.setOnClickListener((v) -> postApiAction(providerManager.registerContentObserver(CallLog.Calls.CONTENT_URI, true, observer)));
        unregisterContentObserver.setOnClickListener((v) -> postApiAction(providerManager.unregisterContentObserver(observer)));
        getLogFromCursor.setOnClickListener((v) -> {
            String selector = String.format("number='%s'", destNumber.getText().toString().trim());
            ResultTask<Cursor> resultTask = providerManager.query(CallLog.Calls.CONTENT_URI, null, selector, null, null);

            resultTask.setOnTaskCompleteListener(result -> {
                Cursor cursor = (Cursor) result.getResultData();
                if (cursor != null && cursor.moveToFirst()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Call Logs\n==============\n");
                    do {
                        String address = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                        String body = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));
                        stringBuilder.append(String.format("Date: %s, Duration: %s\n", address, body));
                    } while (cursor.moveToNext());
                    resultTextView.setText(stringBuilder.toString());
                }
                if (cursor != null) {
                    cursor.close();
                }
            }).invokeTask();
        });
    }

    void postApiAction(ResultTask<?> resultTask) {
        Toast.makeText(TelephonyActivity.this, "Request Posted", Toast.LENGTH_SHORT).show();
        resultTask.setOnTaskCompleteListener(result -> {
            try {
                resultTextView.setText(new ObjectMapper().writeValueAsString(result));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            if(result.isSuccess()) {
                Toast.makeText(TelephonyActivity.this, "Request Succeed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TelephonyActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();
            }

            if(result.hasException()) {
                result.getException().printStackTrace();
            }
        }).invokeTask();
    }

    void setApiButtonsEnabled(boolean enabled) {
        for(MaterialButton button : apiButtons) {
            button.setEnabled(enabled);
        }
    }
}

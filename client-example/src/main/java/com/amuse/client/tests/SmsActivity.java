package com.amuse.client.tests;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.amuse.client.R;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.wrapper.cursor.ContentObserver;
import com.amuse.permit.wrapper.cursor.ProviderManager;
import com.amuse.permit.wrapper.sms.SmsManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class SmsActivity extends AppCompatActivity {

    TextView resultTextView;
    ArrayList<MaterialButton> apiButtons = new ArrayList<>();

    SmsManager smsManager;
    ProviderManager providerManager;
    ContentObserver observer = new ContentObserver() {
        @Override
        public void onChange(Boolean isSelfChanged, Uri uri) {
            String message = String.format("Observer => Uri: %s", uri.toString());
            Log.d("Observer_OnChange", message);
            Toast.makeText(SmsActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sms);
        resultTextView = findViewById(R.id.resultTextView);

        EditText subIdNumber = findViewById(R.id.subIdNumber);
        EditText destNumber = findViewById(R.id.destNumber);
        EditText messageContent = findViewById(R.id.messageContent);

        MaterialButton fetchBasicInfo = findViewById(R.id.fetchBasicInfo);
        MaterialButton getLogFromCursor = findViewById(R.id.getLogFromCursor);
        MaterialButton registerContentObserver = findViewById(R.id.registerContentObserver);
        MaterialButton unregisterContentObserver = findViewById(R.id.unregisterContentObserver);

        MaterialButton getSmscAddress = findViewById(R.id.getSmscAddress);
        MaterialButton getDefaultSmsSubscriptionId = findViewById(R.id.getDefaultSmsSubscriptionId);
        MaterialButton getSmsCapacityOnIcc = findViewById(R.id.getSmsCapacityOnIcc);
        MaterialButton getCarrierConfigValues = findViewById(R.id.getCarrierConfigValues);
        MaterialButton sendTextMessage = findViewById(R.id.sendTextMessage);
        MaterialButton divideMessage = findViewById(R.id.divideMessage);

        apiButtons.add(registerContentObserver);
        apiButtons.add(unregisterContentObserver);
        apiButtons.add(getLogFromCursor);
        apiButtons.add(getSmscAddress);
        apiButtons.add(getDefaultSmsSubscriptionId);
        apiButtons.add(getSmsCapacityOnIcc);
        apiButtons.add(getCarrierConfigValues);
        apiButtons.add(sendTextMessage);
        apiButtons.add(divideMessage);

        setApiButtonsEnabled(false);
        fetchBasicInfo.setOnClickListener((v) -> {
            setApiButtonsEnabled(true);
            String subId = subIdNumber.getText().toString().trim();
            providerManager = ProviderManager.getProviderManager(this);
            if(subId.isEmpty()) {
                smsManager = SmsManager.getDefaultSmsManager(this);
            } else {
                smsManager = SmsManager.createForSubscriptionId(this, Integer.parseInt(subId));
            }
        });

        getSmscAddress.setOnClickListener((v) -> postApiAction(smsManager.getSmscAddress()));
        getDefaultSmsSubscriptionId.setOnClickListener((v) -> postApiAction(smsManager.getDefaultSmsSubscriptionId()));
        getSmsCapacityOnIcc.setOnClickListener((v) -> postApiAction(smsManager.getSmsCapacityOnIcc()));
        getCarrierConfigValues.setOnClickListener((v) -> postApiAction(smsManager.getCarrierConfigValues()));
        divideMessage.setOnClickListener((v) -> postApiAction(smsManager.divideMessage(messageContent.getText().toString().trim())));
        sendTextMessage.setOnClickListener((v) -> postApiAction(smsManager.sendTextMessage(
                destNumber.getText().toString().trim(), null,
                messageContent.getText().toString().trim(), null, null
        )));

        registerContentObserver.setOnClickListener((v) -> postApiAction(providerManager.registerContentObserver(Telephony.Sms.CONTENT_URI, true, observer)));
        unregisterContentObserver.setOnClickListener((v) -> postApiAction(providerManager.unregisterContentObserver(observer)));
        getLogFromCursor.setOnClickListener((v) -> {
            String selector = String.format("address='%s'", destNumber.getText().toString().trim());
            ResultTask<Cursor> resultTask = providerManager.query(Telephony.Sms.CONTENT_URI, null, selector, null, null);

            resultTask.setOnTaskCompleteListener(result -> {
                Cursor cursor = (Cursor) result.getResultData();
                if (cursor != null && cursor.moveToFirst()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Message Logs\n==============\n");
                    do {
                        String address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE));
                        String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                        stringBuilder.append(String.format("Date: %s, Body: %s\n", address, body));
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
        Toast.makeText(SmsActivity.this, "Request Posted", Toast.LENGTH_SHORT).show();
        resultTask.setOnTaskCompleteListener(result -> {
            try {
                resultTextView.setText(new ObjectMapper().writeValueAsString(result));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            if(result.isSuccess()) {
                Toast.makeText(SmsActivity.this, "Request Succeed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SmsActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();
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

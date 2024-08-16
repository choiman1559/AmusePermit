package com.amuse.client.tests;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amuse.client.R;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.process.ProcessConst;
import com.amuse.permit.wrapper.pkg.PackageManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressLint("SetTextI18n")
public class QueryPkgActivity extends AppCompatActivity {

    TextView resultTextView;
    ArrayList<MaterialButton> apiButtons = new ArrayList<>();
    PackageManager packageManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_pkg);

        MaterialButton fetchBasicInfo = findViewById(R.id.fetchBasicInfo);
        MaterialButton setDefaultPackage = findViewById(R.id.setDefaultPackage);
        EditText packageInput = findViewById(R.id.packageInput);
        resultTextView = findViewById(R.id.resultTextView);

        MaterialButton getInstalledApplications = findViewById(R.id.getInstalledApplications);
        MaterialButton queryBroadcastReceivers = findViewById(R.id.queryBroadcastReceivers);
        MaterialButton getPackageInfo = findViewById(R.id.getPackageInfo);

        apiButtons.add(getInstalledApplications);
        apiButtons.add(queryBroadcastReceivers);
        apiButtons.add(getPackageInfo);
        setApiButtonsEnabled(false);

        setDefaultPackage.setOnClickListener((v) -> packageInput.setText("com.android.settings"));
        getInstalledApplications.setOnClickListener((v) -> postApiAction(packageManager.getInstalledApplications(0)));
        queryBroadcastReceivers.setOnClickListener((v) -> postApiAction(packageManager.queryBroadcastReceivers(new Intent(ProcessConst.PACKAGE_BROADCAST_ACTION), 0)));
        getPackageInfo.setOnClickListener((v) -> postApiAction(packageManager.getPackageInfo(packageInput.getText().toString(), 0)));

        fetchBasicInfo.setOnClickListener((v) -> {
            packageManager = PackageManager.getPackageManager(this);
            setApiButtonsEnabled(true);
        });
    }

    void setApiButtonsEnabled(boolean enabled) {
        for(MaterialButton button : apiButtons) {
            button.setEnabled(enabled);
        }
    }

    @SuppressWarnings("unchecked")
    void postApiAction(ResultTask<?> resultTask) {
        Toast.makeText(this, "Request Posted", Toast.LENGTH_SHORT).show();
        resultTask.setOnTaskCompleteListener(result -> {
            Object o = result.getResultData();
            if(o instanceof List) {
                StringBuilder text = new StringBuilder();
                text.append("List of Packages: \n===============\n");
                Object content = ((List<?>) o).get(0);

                if(content instanceof ApplicationInfo) {
                    List<ApplicationInfo> applicationInfos = (List<ApplicationInfo>) o;
                    for(ApplicationInfo appInfo : applicationInfos) {
                        text.append(String.format(Locale.getDefault(), "%s\n", appInfo.packageName));
                    }
                } else if(content instanceof ResolveInfo) {
                    List<ResolveInfo> resolveInfos = (List<ResolveInfo>) o;
                    for(ResolveInfo appInfo : resolveInfos) {
                        text.append(String.format(Locale.getDefault(), "%s\n", appInfo.activityInfo.packageName));
                    }
                }
                resultTextView.setText(text.toString());
            } else if(o instanceof PackageInfo) {
                PackageInfo packageInfo = (PackageInfo) o;
                try {
                    if(packageInfo.packageName != null) {
                        resultTextView.setText(String.format(Locale.getDefault(), "%s\n", new ObjectMapper().writeValueAsString(packageInfo)));
                    }
                } catch (JsonProcessingException e) {
                    resultTextView.setText("Failed to serialize packageInfo Object");
                    e.printStackTrace();
                }
            }

            if(result.hasException()) {
                resultTextView.setText(result.toString());
                result.getException().printStackTrace();
            }

            if(result.isSuccess()) {
                Toast.makeText(this, "Request Succeed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Request Failed", Toast.LENGTH_SHORT).show();
            }
        }).invokeTask();
    }
}

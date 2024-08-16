package com.amuse.client;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amuse.client.tests.FileActivity;
import com.amuse.client.tests.LocateActivity;
import com.amuse.client.tests.QueryPkgActivity;
import com.amuse.permit.Instance;
import com.amuse.permit.data.AppPeer;
import com.amuse.permit.model.ResultTask;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<MaterialButton> activeButtonList = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Instance instance = Instance.getInstance();

        MaterialButton fetchPeer = findViewById(R.id.fetchPeer);
        MaterialButton SelectHeadlessServer = findViewById(R.id.SelectHeadlessServer);
        MaterialButton SelectHeadfulServer = findViewById(R.id.SelectHeadfulServer);
        TextView resultTextView = findViewById(R.id.resultTextView);
        EditText peerPackageName = findViewById(R.id.peerPackageName);

        MaterialButton fileMenu = findViewById(R.id.fileMenu);
        MaterialButton telephonyMenu = findViewById(R.id.telephonyMenu);
        MaterialButton packageMenu = findViewById(R.id.packageMenu);
        MaterialButton locationMenu = findViewById(R.id.locationMenu);

        activeButtonList.add(fileMenu);
        activeButtonList.add(telephonyMenu);
        activeButtonList.add(packageMenu);
        activeButtonList.add(locationMenu);
        setApiButtonsEnabled(false);

        SelectHeadlessServer.setOnClickListener((v) -> peerPackageName.setText("com.amuse.permit.headless"));
        SelectHeadfulServer.setOnClickListener((v) -> peerPackageName.setText("com.amuse.permit.headful"));
        fetchPeer.setOnClickListener((v) -> {
            ResultTask<AppPeer> serverPeer = AppPeer.fetchInformation(this,peerPackageName.getText().toString());
            serverPeer.setOnTaskCompleteListener(result -> {
                try {
                    resultTextView.setText(new ObjectMapper().writeValueAsString(result));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                if(result.isSuccess()) {
                    instance.setServerPeer((AppPeer) result.getResultData());
                    setApiButtonsEnabled(true);
                } else {
                    setApiButtonsEnabled(false);
                }
            }).invokeTask();
            Toast.makeText(this, "Request Posted", Toast.LENGTH_SHORT).show();
        });

        resultTextView.setOnLongClickListener((v) -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Result Text", resultTextView.getText().toString());
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this, "Copied!", Toast.LENGTH_SHORT).show();
            return true;
        });

        //TODO: Add activity for other apis
        fileMenu.setOnClickListener((v) -> startActivity(new Intent(this, FileActivity.class)));
        locationMenu.setOnClickListener((v) -> startActivity(new Intent(this, LocateActivity.class)));
        packageMenu.setOnClickListener((v) -> startActivity(new Intent(this, QueryPkgActivity.class)));
    }

    void setApiButtonsEnabled(boolean enabled) {
        for(MaterialButton button : activeButtonList) {
            button.setEnabled(enabled);
        }
    }
}

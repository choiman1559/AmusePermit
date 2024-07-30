package com.amuse.client;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amuse.client.tests.FileActivity;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<MaterialButton> activeButtonList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialButton fileMenu = findViewById(R.id.fileMenu);
        MaterialButton telephonyMenu = findViewById(R.id.telephonyMenu);
        MaterialButton packageMenu = findViewById(R.id.packageMenu);
        MaterialButton locationMenu = findViewById(R.id.locationMenu);

        activeButtonList.add(fileMenu);
        activeButtonList.add(telephonyMenu);
        activeButtonList.add(packageMenu);
        activeButtonList.add(locationMenu);

        fileMenu.setOnClickListener((v) -> startActivity(new Intent(this, FileActivity.class)));
    }
}

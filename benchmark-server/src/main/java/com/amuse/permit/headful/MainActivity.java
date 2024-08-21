package com.amuse.permit.headful;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amuse.permit.headful.bench.NativeFileTest;
import com.amuse.permit.headful.bench.UnixSocketTest;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialButton testNativeSequentialIO = findViewById(R.id.testNativeSequentialIO);
        MaterialButton testNativeRandomIO = findViewById(R.id.testNativeRandomIO);
        MaterialButton openUnixSocket = findViewById(R.id.openUnixSocket);

        openUnixSocket.setOnClickListener((v) -> UnixSocketTest.openSocket());
        testNativeSequentialIO.setOnClickListener((v) -> new NativeFileTest().openTest());
    }
}

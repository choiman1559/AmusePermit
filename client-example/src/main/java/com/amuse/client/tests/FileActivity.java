package com.amuse.client.tests;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amuse.client.Application;
import com.amuse.client.R;
import com.amuse.permit.process.ProcessConst;

public class FileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.amuse.client.R.layout.activity_test_file);

        if(!Application.hasApiFeature(ProcessConst.ACTION_TYPE_FILE)) {
            Toast.makeText(this, "Server is not Featuring File API! Abort...", Toast.LENGTH_SHORT).show();
            finish();
        }

        EditText filePath = findViewById(R.id.filePath);

    }
}

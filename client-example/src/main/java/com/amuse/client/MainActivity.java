package com.amuse.client;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amuse.permit.model.ResultTask;
import com.amuse.permit.wrapper.file.File;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ResultTask<File> fileTask = File.fileFrom(this, "/storage/emulated/0/Download/file.pdf");
        fileTask.setOnTaskCompleteListener(result -> {
           File file = (File) result.getResultData();
           if(file.exists()) {
               // TODO: blabla...
           }
        });
    }
}

package com.amuse.client.tests;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amuse.client.R;
import com.amuse.permit.Instance;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.process.ProcessConst;
import com.amuse.permit.wrapper.file.File;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FileActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    static File fileInstance;
    ArrayList<MaterialButton> apiButtons = new ArrayList<>();
    TextView resultTextView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.amuse.client.R.layout.activity_test_file);

        if(!Instance.getInstance().getServerPeer().hasApiFeature(ProcessConst.ACTION_TYPE_FILE)) {
            Toast.makeText(this, "Server is not Featuring File API! Abort...", Toast.LENGTH_SHORT).show();
            finish();
        }

        resultTextView = findViewById(R.id.resultTextView);
        EditText filePath = findViewById(R.id.filePath);
        EditText fileContent = findViewById(R.id.fileContent);

        MaterialButton fetchBasicInfo = findViewById(R.id.fetchBasicInfo);
        MaterialButton defaultFile = findViewById(R.id.defaultFile);

        MaterialButton createNewFile = findViewById(R.id.createNewFile);
        MaterialButton delete = findViewById(R.id.delete);
        MaterialButton list = findViewById(R.id.list);
        MaterialButton mkdirs = findViewById(R.id.mkdirs);
        MaterialButton toURI = findViewById(R.id.toURI);
        MaterialButton openFileOutputStream = findViewById(R.id.openFileOutputStream);
        MaterialButton openFileInputStream = findViewById(R.id.openFileInputStream);

        apiButtons.add(createNewFile);
        apiButtons.add(delete);
        apiButtons.add(list);
        apiButtons.add(mkdirs);
        apiButtons.add(toURI);
        apiButtons.add(openFileOutputStream);
        apiButtons.add(openFileInputStream);
        setApiButtonsEnabled(false);

        defaultFile.setOnClickListener((v) -> filePath.setText("/storage/emulated/0/Download/example.txt"));
        fetchBasicInfo.setOnClickListener((v) -> {
            String pathStr = filePath.getText().toString();
            ResultTask<File> fetchFileTask = File.fileFrom(this, pathStr);
            fetchFileTask.setOnTaskCompleteListener(result -> {
                try {
                    resultTextView.setText(new ObjectMapper().writeValueAsString(result));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                if(result.isSuccess()) {
                    fileInstance = (File) result.getResultData();
                    setApiButtonsEnabled(true);
                } else {
                    setApiButtonsEnabled(false);
                }
            }).invokeTask();
        });

        createNewFile.setOnClickListener((v) -> postApiAction(fileInstance.createNewFile()));
        delete.setOnClickListener((v) -> postApiAction(fileInstance.delete()));
        list.setOnClickListener((v) -> postApiAction(fileInstance.listFiles()));
        mkdirs.setOnClickListener((v) -> postApiAction(fileInstance.mkdirs()));
        toURI.setOnClickListener((v) -> postApiAction(fileInstance.toURI()));

        openFileInputStream.setOnClickListener((v) -> {
            ResultTask<InputStream> resultTask = fileInstance.openFileInputStream();
            resultTask.setOnTaskCompleteListener(result -> {
                if(result.isSuccess()) {
                    try {
                        InputStream inputStream = (InputStream) result.getResultData();
                        byte[] buffer = new byte[8912];
                        int readLen;
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        while ((readLen = inputStream.read(buffer)) >= 0) {
                            os.write(buffer, 0, readLen);
                        }
                        String text = new String(os.toByteArray(), StandardCharsets.UTF_8);
                        resultTextView.setText(String.format("File content:\n%s", text));
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(FileActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(FileActivity.this, "Request Succeed", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        resultTextView.setText(new ObjectMapper().writeValueAsString(result));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    Toast.makeText(FileActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();
                }
            }).invokeTask();
        });

        openFileOutputStream.setOnClickListener((v) -> {
            ResultTask<OutputStream> resultTask = fileInstance.openFileOutputStream();
            resultTask.setOnTaskCompleteListener(result -> {
                if(result.isSuccess()) {
                    try {
                        OutputStream outputStream = (OutputStream) result.getResultData();
                        outputStream.write(fileContent.getText().toString().getBytes(StandardCharsets.UTF_8));
                        outputStream.close();
                        Toast.makeText(FileActivity.this, "Request Succeed", Toast.LENGTH_SHORT).show();
                        resultTextView.setText("Write Finished!");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(FileActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        resultTextView.setText(new ObjectMapper().writeValueAsString(result));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    Toast.makeText(FileActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();
                }
            }).invokeTask();
        });

        resultTextView.setOnLongClickListener((v) -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Result Text", resultTextView.getText().toString());
            clipboard.setPrimaryClip(clip);

            Toast.makeText(FileActivity.this, "Copied!", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    void postApiAction(ResultTask<?> resultTask) {
        Toast.makeText(FileActivity.this, "Request Posted", Toast.LENGTH_SHORT).show();
        resultTask.setOnTaskCompleteListener(result -> {
            try {
                resultTextView.setText(new ObjectMapper().writeValueAsString(result));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            if(result.isSuccess()) {
                Toast.makeText(FileActivity.this, "Request Succeed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FileActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();
            }
        }).invokeTask();
    }

    void setApiButtonsEnabled(boolean enabled) {
        for(MaterialButton button : apiButtons) {
            button.setEnabled(enabled);
        }
    }
}

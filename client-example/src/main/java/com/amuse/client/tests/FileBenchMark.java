package com.amuse.client.tests;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amuse.client.R;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.wrapper.file.File;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class FileBenchMark extends AppCompatActivity {

    private static final int BUFFER_SIZE = 1024 * 4; // KB
    private static final int FILE_SIZE_SEQUENTIAL = (1024 * 1024) * 1024; // MB
    private static final int FILE_SIZE_RANDOM = (1024 * 1024) * 64; // MB

    private final static String ipcChannelName = "unixSocketTest_";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_bench);

        MaterialButton startAmusePermitSeq = findViewById(R.id.startAmusePermitSeq);
        MaterialButton startAmusePermitRad = findViewById(R.id.startAmusePermitRad);
        MaterialButton startSocketSeq = findViewById(R.id.startSocketSeq);

        startAmusePermitRad.setOnClickListener((v) -> {

        });

        startAmusePermitSeq.setOnClickListener((v) -> {
            Timer latencyTimer = new Timer();
            Timer seqTimer = new Timer();
            Log.d("AmusePermitTest_SEQ", "Opened Pipe");
            AtomicLong writeTime = new AtomicLong();
            AtomicLong readTime = new AtomicLong();
            AtomicLong latencyTime = new AtomicLong();

            seqTimer.start();

            ResultTask<File> fileTask = File.fileFrom(this, "/storage/emulated/0/Download/fileTest/testUnix.dat");
            fileTask.setOnTaskCompleteListener(result -> {
                if(result.isSuccess()) {
                    File fileObj = (File) result.getResultData();
                    fileObj.delete().setOnTaskCompleteListener(ignore1 -> fileObj.createNewFile().setOnTaskCompleteListener(ignore2 -> {
                        latencyTimer.start();
                        ResultTask<OutputStream> outputStream = fileObj.openFileOutputStream();
                        outputStream.setOnTaskCompleteListener(result1 -> {
                            Log.d("AmusePermitTest_SEQ", "Staring Output Stream");
                            try {
                                OutputStream outputStream1 = (OutputStream) result1.getResultData();
                                latencyTime.set(latencyTimer.stop());
                                Log.d("AmusePermitTest_SEQ", String.format("Latency -> %.2f (ms)", ((float) latencyTime.get() / 1000000)));

                                byte[] buffer = new byte[BUFFER_SIZE];
                                Random rand = new Random();

                                for(int i = 0; i < FILE_SIZE_SEQUENTIAL; i += BUFFER_SIZE) {
                                    rand.nextBytes(buffer);
                                    outputStream1.write(buffer);
                                }

                                writeTime.set(seqTimer.stop());
                                outputStream1.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            seqTimer.start();
                            fileObj.openFileInputStream().setOnTaskCompleteListener(result2 -> {
                                Log.d("AmusePermitTest_SEQ", "Staring Input Stream");
                                try {
                                    InputStream inputStream1 = (InputStream) result2.getResultData();
                                    byte[] buf = new byte[BUFFER_SIZE];
                                    int length;

                                    while ((length = inputStream1.read(buf)) != -1) {
                                        if(length != BUFFER_SIZE) {
                                            Log.d("AmusePermitTest_SEQ", "fragment size mismatch with buffer size: " + length);
                                        }
                                    }

                                    readTime.set(seqTimer.stop());
                                    inputStream1.close();

                                    double resRead = readTime.get() == 0 ? 1.01 : FILE_SIZE_SEQUENTIAL / (1024 * 1024.0) / (readTime.get() / 1000000000.0);
                                    double resWrite = FILE_SIZE_SEQUENTIAL / (1024 * 1024.0) / (writeTime.get() / 1000000000.0);
                                    Log.d("AmusePermitTest_SEQ", String.format("Result-> Read: %.2f (MB/s), Write: %.2f (MB/s)", resRead, resWrite));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }).invokeTask();
                        }).invokeTask();
                    }).invokeTask()).invokeTask();
                }
            }).invokeTask();
        });

        startSocketSeq.setOnClickListener((v) -> {
            long writeTime, readTime;
            Timer timer = new Timer();
            Log.d("UnixSocketTest", "Opened socket");
            timer.start();

            try(LocalSocket clientSocket = new LocalSocket()) {
                byte[] buffer = new byte[BUFFER_SIZE];
                Random rand = new Random();

                clientSocket.connect(new LocalSocketAddress(ipcChannelName + "write"));
                OutputStream outputStream = clientSocket.getOutputStream();

                for(int i = 0; i < FILE_SIZE_SEQUENTIAL; i += BUFFER_SIZE) {
                    rand.nextBytes(buffer);
                    outputStream.write(buffer);
                }
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            writeTime = timer.stop();
            timer.start();

            try {
                Log.d("UnixSocketTest", "Finished socket write. Waiting for read socket...");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try(LocalSocket clientSocket = new LocalSocket()) {
                clientSocket.connect(new LocalSocketAddress(ipcChannelName + "read"));
                InputStream inputStream = clientSocket.getInputStream();
                byte[] buf = new byte[BUFFER_SIZE];
                int length;

                while ((length = inputStream.read(buf)) != -1) {
                    if(length != BUFFER_SIZE) {
                        Log.d("UnixSocketTest", "fragment size mismatch with buffer size: " + length);
                    }
                }
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            readTime = timer.stop();
            double resRead = readTime == 0 ? 1.01 : FILE_SIZE_SEQUENTIAL / (1024 * 1024.0) / (readTime / 1000000000.0);
            double resWrite = FILE_SIZE_SEQUENTIAL / (1024 * 1024.0) / (writeTime / 1000000000.0);
            Log.d("UnixSocketTest", String.format("Result-> Read: %.2f (MB/s), Write: %.2f (MB/s)", resRead, resWrite));
        });
    }
}

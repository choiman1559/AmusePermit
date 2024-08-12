package com.amuse.permit.headful.bench;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class NativeFileTest {

    private static final int BUFFER_SIZE = 1024 * 4; // KB
    private static final int FILE_SIZE_SEQUENTIAL = 1024 * 1024 * 1024; // MB

    private static final String FILE_NAME = "benchmark_%s.dat";
    private final Timer timer = new Timer();
    private final Timer latencyTimer = new Timer();

    private long writeResult, readResult, latency;
    private File fileDir;
    private File file;

    public void openTest() {
        fileDir = new File("/storage/emulated/0/Download/fileTest/");
        file = new File(fileDir, "testFile.dat");
        file.delete();

        latencyTimer.start();
        timer.start();

        try {
            OutputStream outputStream1 = new FileOutputStream(file);
            latency = latencyTimer.stop();
            Log.d("AmusePermitTest_SEQ", String.format("Latency -> %.2f (ms)", ((float) latency / 1000000)));

            byte[] buffer = new byte[BUFFER_SIZE];
            Random rand = new Random();

            for(int i = 0; i < FILE_SIZE_SEQUENTIAL; i += BUFFER_SIZE) {
                rand.nextBytes(buffer);
                outputStream1.write(buffer);
            }

            writeResult = timer.stop();
            outputStream1.close();
            timer.start();

            InputStream inputStream1 = new FileInputStream(file);
            byte[] buf = new byte[BUFFER_SIZE];
            int length;

            while ((length = inputStream1.read(buf)) != -1) {
                if(length != BUFFER_SIZE) {
                    Log.d("AmusePermitTest_SEQ", "fragment size mismatch with buffer size: " + length);
                }
            }

            inputStream1.close();
            readResult = timer.stop();

            double resRead = readResult == 0 ? 1.01 : FILE_SIZE_SEQUENTIAL / (1024 * 1024.0) / (readResult / 1000000000.0);
            double resWrite = FILE_SIZE_SEQUENTIAL / (1024 * 1024.0) / (writeResult / 1000000000.0);
            Log.d("AmusePermitTest_SEQ", String.format("Result-> Read: %.2f (MB/s), Write: %.2f (MB/s)", resRead, resWrite));
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
}

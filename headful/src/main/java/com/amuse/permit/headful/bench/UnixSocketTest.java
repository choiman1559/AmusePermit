package com.amuse.permit.headful.bench;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UnixSocketTest {

    public final static int BUFFER_SIZE = 1024 * 4;
    public final static String ipcChannelName = "unixSocketTest_";
    public final static File fileDir = new File("/storage/emulated/0/Download/fileTest/");

    public final static Thread localThread = new Thread(() -> {
        Timer timer = new Timer();
        File testFile = new File(fileDir, "testUnix.dat");

        try {
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            if(testFile.exists()) {
                testFile.delete();
            }

            testFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        long latencyCount = 0L;
        timer.start();

        try (LocalServerSocket serverSocket = new LocalServerSocket(ipcChannelName + "write")) {
            latencyCount += timer.stop();

            while (true) {
                LocalSocket clientSocket = serverSocket.accept();
                if (clientSocket.isConnected()) {
                    timer.start();
                    InputStream inputStream = clientSocket.getInputStream();
                    FileOutputStream outputStream = new FileOutputStream(testFile);
                    latencyCount += timer.stop();

                    byte[] buf = new byte[BUFFER_SIZE];
                    int length;
                    while ((length = inputStream.read(buf)) != -1) {
                        outputStream.write(buf, 0, length);
                    }
                    clientSocket.close();
                    break;
                }
            }
            Log.d("UnixSocketTest", "Socket Write Successfully Finished, Latency: " + (float) latencyCount / 1000000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (LocalServerSocket serverSocket = new LocalServerSocket(ipcChannelName + "read")) {
            while (true) {
                LocalSocket clientSocket = serverSocket.accept();
                if (clientSocket.isConnected()) {
                    OutputStream outputStream = clientSocket.getOutputStream();
                    FileInputStream inputStream = new FileInputStream(testFile);

                    byte[] buf = new byte[BUFFER_SIZE];
                    int length;
                    while ((length = inputStream.read(buf)) != -1) {
                        outputStream.write(buf, 0, length);
                    }
                    clientSocket.close();
                    break;
                }
            }
            Log.d("UnixSocketTest", "Socket Read Successfully Finished");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("UnixSocketTest", "Socket Thread Finished");
    });

    public static void openSocket() {
        Log.d("UnixSocketTest", "Opened socket");

        if(localThread.isAlive()) {
            localThread.interrupt();
            Log.d("UnixSocketTest", "Socket Forced Finished");
        } else {
            localThread.isInterrupted();
            localThread.start();
            Log.d("UnixSocketTest", "Socket Started");
        }
    }
}

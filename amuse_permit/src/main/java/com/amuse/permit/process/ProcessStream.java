package com.amuse.permit.process;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;

import com.amuse.permit.Instance;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class ProcessStream extends ContentProvider {

    public static final HashMap<String, InputStream> inputStreamMap = new HashMap<>();
    public static final HashMap<String, OutputStream> outputStreamMap = new HashMap<>();

    private static final int FILE = 1;
    private static UriMatcher uriMatcher;

    @Override
    public boolean onCreate() {
        return true;
    }

    private static void initializeUriMatcher() {
        if(uriMatcher == null) {
            Instance instance = Instance.getInstance();
            String AUTHORITY = String.format("%s$%s", ProcessConst.PACKAGE_STREAM, instance.getAppPackageName());

            uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
            uriMatcher.addURI(AUTHORITY, "*", FILE);
        }
    }

    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        initializeUriMatcher();
        int match = uriMatcher.match(uri);
        if (match != FILE) {
            throw new FileNotFoundException("Unsupported URI: " + uri);
        }

        try {
            String[] ipcChannelUri = uri.toString().split("/");
            String ipcChannelName = ipcChannelUri[ipcChannelUri.length - 1];
            String ipcChannelType = ipcChannelName.split("_")[1];

            ParcelFileDescriptor[] pipe = ParcelFileDescriptor.createReliablePipe();
            ParcelFileDescriptor destFileDescriptor;
            ParcelFileDescriptor targetFileDescriptor;
            Thread transportThread;

            switch (ipcChannelType) {
                case ProcessConst.STREAM_INPUT:
                    destFileDescriptor = pipe[0];
                    targetFileDescriptor = pipe[1];
                    transportThread = new FileReadTransferThread(ipcChannelName, new ParcelFileDescriptor.AutoCloseOutputStream(targetFileDescriptor));
                    break;

                case ProcessConst.STREAM_OUTPUT:
                    destFileDescriptor = pipe[1];
                    targetFileDescriptor = pipe[0];
                    transportThread = new FileWriteTransferThread(ipcChannelName, new ParcelFileDescriptor.AutoCloseInputStream(targetFileDescriptor));
                    break;

                default:
                    throw new IllegalAccessException("Unsupported IPC type: " + ipcChannelName);
            }

            transportThread.start();
            if(targetFileDescriptor.canDetectErrors()) {
                targetFileDescriptor.checkError();
            }

            return destFileDescriptor;
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
            throw new FileNotFoundException("Error creating pipe");
        }
    }

    private static class FileWriteTransferThread extends Thread {
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private final String channelName;

        FileWriteTransferThread(String channelName, InputStream inputStream) {
            this.channelName = channelName;
            this.inputStream = inputStream;
            this.outputStream = ProcessStream.outputStreamMap.get(channelName);
        }

        @Override
        public void run() {
            try {
                byte[] buf = new byte[8192];
                int length;
                while ((length = inputStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    outputStream.close();
                    ProcessStream.inputStreamMap.remove(channelName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class FileReadTransferThread extends Thread {
        private final OutputStream outputStream;
        private final InputStream inputStream;
        private final String channelName;

        FileReadTransferThread(String channelName, OutputStream outputStream) {
            this.channelName = channelName;
            this.outputStream = outputStream;
            this.inputStream = ProcessStream.inputStreamMap.get(channelName);
        }

        @Override
        public void run() {
            try {
                byte[] buf = new byte[8192];
                int length;
                while ((length = inputStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    outputStream.close();
                    ProcessStream.inputStreamMap.remove(channelName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

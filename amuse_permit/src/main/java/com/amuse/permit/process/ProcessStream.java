package com.amuse.permit.process;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class ProcessStream extends ContentProvider {

    public static final HashMap<String, FileInputStream> inputStreamMap = new HashMap<>();
    public static final HashMap<String, FileOutputStream> outputStreamMap = new HashMap<>();

    private static final String AUTHORITY = ProcessConst.PACKAGE_STREAM;
    private static final int FILE = 1;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, "*", FILE);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        int match = uriMatcher.match(uri);
        if (match != FILE) {
            throw new FileNotFoundException("Unsupported URI: " + uri);
        }

        try {
            String[] ipcChannelUri = uri.toString().split("/");
            String ipcChannelName = ipcChannelUri[ipcChannelUri.length - 1];

            ParcelFileDescriptor[] pipe = ParcelFileDescriptor.createReliablePipe();
            ParcelFileDescriptor destFileDescriptor;
            ParcelFileDescriptor targetFileDescriptor;
            Thread transportThread;

            switch (ipcChannelName) {
                case ProcessConst.STREAM_OUTPUT:
                    destFileDescriptor = pipe[0];
                    targetFileDescriptor = pipe[1];
                    transportThread = new FileReadTransferThread(ipcChannelName, new ParcelFileDescriptor.AutoCloseOutputStream(targetFileDescriptor));
                    break;

                case ProcessConst.STREAM_INPUT:
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
        private final FileOutputStream outputStream;
        private final String channelName;

        FileWriteTransferThread(String channelName, InputStream inputStream) {
            this.channelName = channelName;
            this.inputStream = inputStream;
            this.outputStream = ProcessStream.outputStreamMap.get(channelName);
        }

        @Override
        public void run() {
            Log.d("ddd", "ReFileTransferThread.FileWriteTransferThread");
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
                Log.d("ddd", "ReFileTransferThread.FileTransferThread22222");
            }
        }
    }

    private static class FileReadTransferThread extends Thread {
        private final OutputStream outputStream;
        private final FileInputStream inputStream;
        private final String channelName;

        FileReadTransferThread(String channelName, OutputStream outputStream) {
            this.channelName = channelName;
            this.outputStream = outputStream;
            this.inputStream = ProcessStream.inputStreamMap.get(channelName);
        }

        @Override
        public void run() {
            Log.d("ddd", "ReFileTransferThread.FileReadTransferThread");
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
                Log.d("ddd", "ReFileTransferThread.FileTransferThread22222");
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

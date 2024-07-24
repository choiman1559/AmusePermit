package com.amuse.permit.wrapper.file;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.amuse.permit.model.Annotations;
import com.amuse.permit.model.NameFilters;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.model.Wrappable;

import java.io.IOException;
import java.net.URI;

public class FileNativeWrapper extends FileModel {

    private final java.io.File baseFileObj;

    @Annotations.ResponserSide
    @SuppressLint("UsableSpace")
    public FileNativeWrapper(java.io.File baseFileObj) {
        this.baseFileObj = baseFileObj;
        this.canExecute = baseFileObj.canExecute();
        this.canRead = baseFileObj.canRead();
        this.canWrite = baseFileObj.canWrite();
        this.exists = baseFileObj.exists();
        this.absolutePath = baseFileObj.getAbsolutePath();
        this.freeSpace = baseFileObj.getFreeSpace();
        this.name = baseFileObj.getName();
        this.parent = baseFileObj.getParent();
        this.path = baseFileObj.getPath();
        this.totalSpace = baseFileObj.getTotalSpace();
        this.usableSpace = baseFileObj.getUsableSpace();
        this.hashCode = baseFileObj.hashCode();
        this.isAbsolute = baseFileObj.isAbsolute();
        this.isDirectory = baseFileObj.isDirectory();
        this.isFile = baseFileObj.isFile();
        this.isHidden = baseFileObj.isHidden();
        this.lastModified = baseFileObj.lastModified();
        this.length = baseFileObj.length();

        try {
            this.canonicalPath = baseFileObj.getCanonicalPath();
        } catch (IOException e) {
            this.canonicalPath = null;
        }
    }

    @Annotations.ResponserSide
    public boolean createNewFile() {
        try {
            return baseFileObj.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    @Annotations.ResponserSide
    public boolean delete() {
        return baseFileObj.delete();
    }

    @Annotations.ResponserSide
    public String[] list() {
        return baseFileObj.list();
    }

    @Annotations.ResponserSide
    public FileNativeWrapper[] listFiles() {
        java.io.File[] listFile = baseFileObj.listFiles();
        if(listFile != null) {
            FileNativeWrapper[] files = new FileNativeWrapper[listFile.length];
            for(int i = 0; i < files.length; i++) {
                files[i] = new FileNativeWrapper(listFile[i]);
            }

            return files;
        }
        return new FileNativeWrapper[0];
    }

    @Annotations.ResponserSide
    public static FileNativeWrapper[] listRoots() {
        java.io.File[] listFile = java.io.File.listRoots();
        FileNativeWrapper[] files = new FileNativeWrapper[listFile.length];
        for(int i = 0; i < files.length; i++) {
            files[i] = new FileNativeWrapper(listFile[i]);
        }
        return files;
    }

    @Annotations.ResponserSide
    public boolean mkdir() {
        return baseFileObj.mkdir();
    }

    @Annotations.ResponserSide
    public boolean mkdirs() {
        return baseFileObj.mkdirs();
    }

    @Annotations.ResponserSide
    public boolean renameTo(File dest) {
        return baseFileObj.renameTo(new java.io.File(dest.absolutePath));
    }

    @Annotations.ResponserSide
    public boolean setExecutable(boolean executable) {
        return baseFileObj.setExecutable(executable);
    }

    @Annotations.ResponserSide
    public boolean setLastModified(long time) {
        return baseFileObj.setLastModified(time);
    }

    @Annotations.ResponserSide
    public boolean setReadOnly() {
        return baseFileObj.setReadOnly();
    }

    @Annotations.ResponserSide
    public boolean setReadable(boolean readable) {
        return baseFileObj.setReadable(readable);
    }

    @Annotations.ResponserSide
    public boolean setWritable(boolean writable) {
        return baseFileObj.setWritable(writable);
    }

    @Annotations.ResponserSide
    public URI toURI() {
        return baseFileObj.toURI();
    }

    @Override
    public boolean checkPermissionGranted(@NonNull Context context) {
        boolean isPermissionGranted = false;
        if (Build.VERSION.SDK_INT >= 30 && Environment.isExternalStorageManager()) {
            isPermissionGranted = true;
        } else if (Build.VERSION.SDK_INT > 28 &&
                (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            isPermissionGranted = true;
        } else if (Build.VERSION.SDK_INT <= 28) {
            isPermissionGranted = true;
        }

        return isPermissionGranted;
    }
}

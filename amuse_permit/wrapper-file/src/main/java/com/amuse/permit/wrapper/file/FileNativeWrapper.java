package com.amuse.permit.wrapper.file;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.model.Annotations;
import com.amuse.permit.process.ServiceProcess;
import com.amuse.permit.model.Wrappable;

import java.io.IOException;
import java.net.URI;

@SuppressWarnings("unused")
@Annotations.ResponserSide
public class FileNativeWrapper extends FileModel {

    private final java.io.File baseFileObj;

    public FileNativeWrapper() {
        baseFileObj = Environment.getExternalStorageDirectory();
    }

    @SuppressLint("UsableSpace")
    @Annotations.Constructor
    private FileNativeWrapper(java.io.File baseFileObj) {
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

    @Override
    public Wrappable createServerInstance(@NonNull ArgsInfo packetData) {
        String filePath = (String) packetData.getData(0);
        java.io.File baseFile = new java.io.File(filePath);
        return new FileNativeWrapper(baseFile);
    }

    @Annotations.NativeWrapper
    public static FileModel createTempFile(String prefix, String suffix) throws IOException {
        return (FileModel) ServiceProcess.convertToFinalFormat(new FileNativeWrapper(java.io.File.createTempFile(prefix, suffix)), FileModel.class);
    }

    @Annotations.NativeWrapper
    public boolean createNewFile() {
        try {
            return baseFileObj.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    @Annotations.NativeWrapper
    public boolean delete() {
        return baseFileObj.delete();
    }

    @Annotations.NativeWrapper
    public String[] list() {
        return baseFileObj.list();
    }

    @Annotations.NativeWrapper
    public FileModel[] listFiles() {
        java.io.File[] listFile = baseFileObj.listFiles();
        if(listFile != null) {
            FileModel[] files = new FileModel[listFile.length];
            for(int i = 0; i < files.length; i++) {
                files[i] = (FileModel) ServiceProcess.convertToFinalFormat(new FileNativeWrapper(listFile[i]), FileModel.class);
            }

            return files;
        }
        return new FileModel[0];
    }

    @Annotations.NativeWrapper
    public static FileModel[] listRoots() {
        java.io.File[] listFile = java.io.File.listRoots();
        FileModel[] files = new FileModel[listFile.length];
        for(int i = 0; i < files.length; i++) {
            files[i] = (FileModel) ServiceProcess.convertToFinalFormat(new FileNativeWrapper(listFile[i]), FileModel.class);
        }
        return files;
    }

    @Annotations.NativeWrapper
    public boolean mkdir() {
        return baseFileObj.mkdir();
    }

    @Annotations.NativeWrapper
    public boolean mkdirs() {
        return baseFileObj.mkdirs();
    }

    @Annotations.NativeWrapper
    public boolean renameTo(File dest) {
        return baseFileObj.renameTo(new java.io.File(dest.getAbsolutePath()));
    }

    @Annotations.NativeWrapper
    public boolean setExecutable(boolean executable) {
        return baseFileObj.setExecutable(executable);
    }

    @Annotations.NativeWrapper
    public boolean setLastModified(long time) {
        return baseFileObj.setLastModified(time);
    }

    @Annotations.NativeWrapper
    public boolean setReadOnly() {
        return baseFileObj.setReadOnly();
    }

    @Annotations.NativeWrapper
    public boolean setReadable(boolean readable) {
        return baseFileObj.setReadable(readable);
    }

    @Annotations.NativeWrapper
    public boolean setWritable(boolean writable) {
        return baseFileObj.setWritable(writable);
    }

    @Annotations.NativeWrapper
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

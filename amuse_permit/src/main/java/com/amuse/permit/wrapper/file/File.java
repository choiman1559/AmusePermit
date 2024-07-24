package com.amuse.permit.wrapper.file;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amuse.permit.model.Annotations;
import com.amuse.permit.model.NameFilters;
import com.amuse.permit.model.ResultTask;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

@SuppressWarnings("unused")
public class File extends FileModel {

    @Annotations.RequesterSide
    public static ResultTask<File> fileFrom(@NonNull String pathname) {
        ResultTask<File> resultTask = new ResultTask<>();
        ResultTask.Result<File> resultObj = new ResultTask.Result<>();

        File file = new File();
        file.absolutePath = pathname;
        resultObj.setResultData(file);

        resultTask.callCompleteListener(resultObj);
        return resultTask;
    }

    @Annotations.RequesterSide
    public static ResultTask<File> fileFrom(@Nullable String parent, @NonNull String child) {
        return fileFrom(parent + child);
    }

    @Annotations.RequesterSide
    public static ResultTask<File> fileFrom(@Nullable File parent, @NonNull String child) {
        if (parent != null) {
            parent.checkIsFetched();
            return fileFrom(parent.getAbsolutePath() + child);
        }
        throw new IllegalStateException("Parent directory file object is not initialized");
    }

    @Annotations.RequesterSide
    public static ResultTask<File> fileFrom(@NonNull URI uri) {
        return fileFrom(uri.getPath());
    }

    @Annotations.RequesterSide
    public boolean canExecute() {
        checkIsFetched();
        return canExecute;
    }

    @Annotations.RequesterSide
    public boolean canRead() {
        checkIsFetched();
        return canRead;
    }

    @Annotations.RequesterSide
    public boolean canWrite() {
        checkIsFetched();
        return canWrite;
    }

    @Annotations.RequesterSide
    public boolean exists() {
        checkIsFetched();
        return exists;
    }

    @Annotations.RequesterSide
    public String getAbsolutePath() {
        checkIsFetched();
        return absolutePath;
    }

    @Annotations.RequesterSide
    public String getCanonicalPath() {
        checkIsFetched();
        return canonicalPath;
    }

    @Annotations.RequesterSide
    public long getFreeSpace() {
        checkIsFetched();
        return freeSpace;
    }

    @Annotations.RequesterSide
    public String getName() {
        checkIsFetched();
        return name;
    }

    @Annotations.RequesterSide
    public String getParent() {
        checkIsFetched();
        return parent;
    }

    @Annotations.RequesterSide
    public String getPath() {
        checkIsFetched();
        return path;
    }

    @Annotations.RequesterSide
    public long getTotalSpace() {
        checkIsFetched();
        return totalSpace;
    }

    @Annotations.RequesterSide
    public long getUsableSpace() {
        checkIsFetched();
        return usableSpace;
    }

    @Annotations.RequesterSide
    public boolean isAbsolute() {
        checkIsFetched();
        return isAbsolute;
    }

    @Annotations.RequesterSide
    public boolean isDirectory() {
        checkIsFetched();
        return isDirectory;
    }

    @Annotations.RequesterSide
    public boolean isFile() {
        checkIsFetched();
        return isFile;
    }

    @Annotations.RequesterSide
    public boolean isHidden() {
        checkIsFetched();
        return isHidden;
    }

    @Annotations.RequesterSide
    public long lastModified() {
        checkIsFetched();
        return lastModified;
    }

    @Annotations.RequesterSide
    public long length() {
        checkIsFetched();
        return length;
    }

    @Annotations.RequesterSide
    public ResultTask<Boolean> createNewFile() {
        postRequest().setOnTaskCompleteListener(result -> {

        });
    }

    @Annotations.RequesterSide
    public static ResultTask<File> createTempFile(String prefix, String suffix) {

    }

    public ResultTask<Boolean> delete() {

    }

    public ResultTask<String[]> list() {

    }

    public ResultTask<String[]> list(NameFilters.NameFilter<String> filters) {

    }

    public ResultTask<File[]> listFiles() {

    }

    public ResultTask<File[]> listFiles(NameFilters.NameFilter<String> filters) {

    }

    public ResultTask<File[]> listFiles(NameFilters.NameFilterWithExtra<String, File> filters) {

    }

    public static ResultTask<File[]> listRoots() {

    }

    public ResultTask<Boolean> mkdir() {

    }

    public ResultTask<Boolean> mkdirs() {

    }

    public ResultTask<Boolean> renameTo(File dest) {

    }

    public ResultTask<Boolean> setExecutable(boolean executable) {

    }

    public ResultTask<Boolean> setLastModified(long time) {

    }

    public ResultTask<Boolean> setReadOnly() {

    }

    public ResultTask<Boolean> setReadable(boolean readable) {

    }

    public ResultTask<Boolean> setWritable(boolean writable) {

    }

    public ResultTask<URI> toURI() {

    }

    @Annotations.RequesterSide
    public ResultTask<InputStream> openFileInputStream() {

    }

    @Annotations.RequesterSide
    public ResultTask<OutputStream> openFileOutputStream() {

    }

    @Annotations.RequesterSide
    @Override
    public int hashCode() {
        checkIsFetched();
        return hashCode;
    }

    @Annotations.RequesterSide
    @NonNull
    @Override
    public String toString() {
        return this.getPath();
    }

    @Annotations.RequesterSide
    @Override
    public boolean equals(Object object) {
        if (object instanceof File) {
            return (((File) object).getAbsolutePath()).equals(this.getAbsolutePath());
        } else return false;
    }

    @Override
    public ResultTask<Intent> postRequest(Object... args) {
        return super.postRequest(args);
    }
}

package com.amuse.permit.wrapper.file;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amuse.permit.Instance;
import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.model.Annotations;
import com.amuse.permit.model.NameFilters;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.process.ProcessConst;
import com.amuse.permit.process.action.ResultCreator;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;

@Annotations.RequesterSide
@SuppressWarnings("unused")
public class File extends FileModel {

    private Context context;

    @Annotations.Constructor
    public static ResultTask<File> fileFrom(Context context, @NonNull String pathname) {
        ResultTask<File> resultTask = new ResultTask<>();
        ResultTask.Result<File> resultObj = new ResultTask.Result<>();

        File file = new File();
        file.absolutePath = pathname;
        resultObj.setResultData(file);

        resultTask.callCompleteListener(resultObj);
        return resultTask;
    }

    @Annotations.Constructor
    public static ResultTask<File> fileFrom(Context context, @Nullable String parent, @NonNull String child) {
        return fileFrom(context, parent + child);
    }

    @Annotations.Constructor
    public static ResultTask<File> fileFrom(Context context, @Nullable File parent, @NonNull String child) {
        if (parent != null) {
            parent.checkIsFetched();
            return fileFrom(context, parent.getAbsolutePath() + child);
        }
        throw new IllegalStateException("Parent directory file object is not initialized");
    }

    @Annotations.Constructor
    public static ResultTask<File> fileFrom(Context context, @NonNull URI uri) {
        return fileFrom(context, uri.getPath());
    }

    @Annotations.StaticMethod
    @Annotations.Constructor
    public static File fileStatic(Context context) {
        File file = new File();
        file.context = context;
        return file;
    }

    public boolean canExecute() {
        checkIsFetched();
        return canExecute;
    }

    public boolean canRead() {
        checkIsFetched();
        return canRead;
    }

    public boolean canWrite() {
        checkIsFetched();
        return canWrite;
    }

    public boolean exists() {
        checkIsFetched();
        return exists;
    }

    public String getAbsolutePath() {
        checkIsFetched();
        return absolutePath;
    }

    public String getCanonicalPath() {
        checkIsFetched();
        return canonicalPath;
    }

    public long getFreeSpace() {
        checkIsFetched();
        return freeSpace;
    }

    public String getName() {
        checkIsFetched();
        return name;
    }

    public String getParent() {
        checkIsFetched();
        return parent;
    }

    public String getPath() {
        checkIsFetched();
        return path;
    }

    public long getTotalSpace() {
        checkIsFetched();
        return totalSpace;
    }

    public long getUsableSpace() {
        checkIsFetched();
        return usableSpace;
    }

    public boolean isAbsolute() {
        checkIsFetched();
        return isAbsolute;
    }

    public boolean isDirectory() {
        checkIsFetched();
        return isDirectory;
    }

    public boolean isFile() {
        checkIsFetched();
        return isFile;
    }

    public boolean isHidden() {
        checkIsFetched();
        return isHidden;
    }

    @Annotations.RequesterSide
    public long lastModified() {
        checkIsFetched();
        return lastModified;
    }

    public long length() {
        checkIsFetched();
        return length;
    }

    public ResultTask<Boolean> createNewFile() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData(Boolean.class)).postMethodProcess(context);
    }

    @Annotations.StaticMethod
    public ResultTask<File> createTempFile(String prefix, String suffix) {
        return new ResultCreator<File>(buildMethodCallPacketData(File.class, prefix, suffix)).postMethodProcess(context);
    }

    public ResultTask<Boolean> delete() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData(Boolean.class)).postMethodProcess(context);
    }

    public ResultTask<String[]> list() {
        return new ResultCreator<String[]>(buildMethodCallPacketData(String[].class)).postMethodProcess(context);
    }

    public ResultTask<String[]> list(NameFilters.NameFilter<String> filters) {
        ResultTask<String[]> finalResult = new ResultTask<>();
        ResultTask<String[]> rawResult = new ResultCreator<String[]>(buildMethodCallPacketData(String[].class))
                .postMethodProcess(context);

        rawResult.setOnTaskCompleteListener(result -> {
            ResultTask.Result<String[]> resultObj = new ResultTask.Result<>();
            resultObj.setSuccess(result.isSuccess());
            resultObj.setHasException(result.hasException());
            resultObj.setException(result.getException());

            if(result.isSuccess()) {
                ArrayList<String> resultList = new ArrayList<>();
                for(String str : (String[]) result.getResultData()) {
                    if(filters.accept(str)) {
                        resultList.add(str);
                    }
                }
                resultObj.setResultData(resultList.toArray());
            }
            finalResult.callCompleteListener(resultObj);
        });

        finalResult.mOnInvokeAttached = result1 -> rawResult.invokeTask();
        return finalResult;
    }

    public ResultTask<File[]> listFiles() {
        return new ResultCreator<File[]>(buildMethodCallPacketData(File[].class)).postMethodProcess(context);
    }

    public ResultTask<File[]> listFiles(NameFilters.NameFilter<String> filters) {
        ResultTask<File[]> finalResult = new ResultTask<>();
        ResultTask<File[]> rawResult = new ResultCreator<File[]>(buildMethodCallPacketData(File[].class))
                .postMethodProcess(context);

        rawResult.setOnTaskCompleteListener(result -> {
            ResultTask.Result<File[]> resultObj = new ResultTask.Result<>();
            resultObj.setSuccess(result.isSuccess());
            resultObj.setHasException(result.hasException());
            resultObj.setException(result.getException());

            if(result.isSuccess()) {
                ArrayList<File> resultList = new ArrayList<>();
                for(File files : (File[]) result.getResultData()) {
                    if(filters.accept(files.name)) {
                        resultList.add(files);
                    }
                }
                resultObj.setResultData(resultList.toArray());
            }
            finalResult.callCompleteListener(resultObj);
        });

        finalResult.mOnInvokeAttached = result1 -> rawResult.invokeTask();
        return finalResult;
    }

    public ResultTask<File[]> listFiles(NameFilters.NameFilterWithExtra<String, File> filters) {
        ResultTask<File[]> finalResult = new ResultTask<>();
        ResultTask<File[]> rawResult = new ResultCreator<File[]>(buildMethodCallPacketData(File[].class))
                .postMethodProcess(context);

        rawResult.setOnTaskCompleteListener(result -> {
            ResultTask.Result<File[]> resultObj = new ResultTask.Result<>();
            resultObj.setSuccess(result.isSuccess());
            resultObj.setHasException(result.hasException());
            resultObj.setException(result.getException());

            if(result.isSuccess()) {
                ArrayList<File> resultList = new ArrayList<>();
                for(File files : (File[]) result.getResultData()) {
                    if(filters.accept(files.name, files)) {
                        resultList.add(files);
                    }
                }
                resultObj.setResultData(resultList.toArray());
            }
            finalResult.callCompleteListener(resultObj);
        });

        finalResult.mOnInvokeAttached = result1 -> rawResult.invokeTask();
        return finalResult;
    }

    @Annotations.StaticMethod
    public ResultTask<File[]> listRoots() {
        return new ResultCreator<File[]>(buildMethodCallPacketData(File[].class)).postMethodProcess(context);
    }

    public ResultTask<Boolean> mkdir() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData(Boolean.class)).postMethodProcess(context);
    }

    public ResultTask<Boolean> mkdirs() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData(Boolean.class)).postMethodProcess(context);
    }

    public ResultTask<Boolean> renameTo(File dest) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData(Boolean.class, dest)).postMethodProcess(context);
    }

    public ResultTask<Boolean> setExecutable(boolean executable) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData(Boolean.class, executable)).postMethodProcess(context);
    }

    public ResultTask<Boolean> setLastModified(long time) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData(Boolean.class, time)).postMethodProcess(context);
    }

    public ResultTask<Boolean> setReadOnly() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData(Boolean.class)).postMethodProcess(context);
    }

    public ResultTask<Boolean> setReadable(boolean readable) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData(Boolean.class, readable)).postMethodProcess(context);
    }

    public ResultTask<Boolean> setWritable(boolean writable) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData(Boolean.class, writable)).postMethodProcess(context);
    }

    public ResultTask<URI> toURI() {
        return new ResultCreator<URI>(buildMethodCallPacketData(URI.class)).postMethodProcess(context);
    }

    public ResultTask<InputStream> openFileInputStream() {
        return new ResultCreator<InputStream>(buildMethodCallPacketData(InputStream.class)).postMethodProcess(context);
    }

    public ResultTask<OutputStream> openFileOutputStream() {
        return new ResultCreator<OutputStream>(buildMethodCallPacketData(OutputStream.class)).postMethodProcess(context);
    }

    @Override
    public int hashCode() {
        checkIsFetched();
        return hashCode;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getPath();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof File) {
            return (((File) object).getAbsolutePath()).equals(this.getAbsolutePath());
        } else return false;
    }

    private static String getMethodName()
    {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        return ste[ste.length - 4].getMethodName();
    }

    private PacketData buildMethodCallPacketData(Class<?> parameterCls, Object... args) {
        Instance instance = Instance.getInstance();
        PacketData packet = new PacketData();
        ArgsInfo argsInfo = new ArgsInfo();

        String methodName =  getMethodName();
        String ticket = String.format("%s@%s", methodName, System.currentTimeMillis());

        argsInfo.put(getAbsolutePath());
        argsInfo.put(parameterCls, methodName);

        for(Object arg : args) {
            argsInfo.put(arg);
        }

        packet.argsInfo = argsInfo;
        packet.fromPackageName = instance.getServerPeer().getPackageName();
        packet.ticketId = ticket;
        packet.actionType = ProcessConst.ACTION_TYPE_FILE;

        return packet;
    }
}

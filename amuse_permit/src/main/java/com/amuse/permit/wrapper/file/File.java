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
import com.amuse.permit.process.ServiceProcess;
import com.amuse.permit.process.ProcessConst;
import com.amuse.permit.process.ProcessRoute;
import com.amuse.permit.process.action.ClientAction;
import com.amuse.permit.process.action.ResultCreator;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;

@Annotations.RequesterSide
@SuppressWarnings("unused")
public class File extends FileModel {

    @JsonIgnore
    protected Context context;

    @Annotations.Constructor
    public static ResultTask<File> fileFrom(Context context, @NonNull String pathname) {
        ResultTask<File> resultTask = new ResultTask<>();
        resultTask.mOnInvokeAttached = result -> {
            PacketData packetData = new PacketData();
            ArgsInfo argsInfo = new ArgsInfo();

            packetData.ticketId = String.format("file@%s_%s", pathname, System.currentTimeMillis());
            packetData.fromPackageName = Instance.getInstance().getServerPeer().getPackageName();
            argsInfo.put(pathname);

            ClientAction clientAction = new ClientAction(context, packetData);
            clientAction.pushClass(ProcessConst.ACTION_TYPE_FILE, argsInfo);

            ProcessRoute.registerInnerResultTask(context, packetData.ticketId, resultTask);
            clientAction.send();
        };
        return resultTask;
    }

    @Annotations.Constructor
    public static ResultTask<File> fileFrom(Context context, @Nullable String parent, @NonNull String child) {
        return fileFrom(context, parent + child);
    }

    @Annotations.Constructor
    public static ResultTask<File> fileFrom(Context context, @Nullable File parent, @NonNull String child) {
        if (parent != null && parent.checkIsFetched()) {
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

    @JsonGetter("canExecute")
    public boolean canExecute() {
        return canExecute;
    }

    @JsonGetter("canRead")
    public boolean canRead() {
        return canRead;
    }

    @JsonGetter("canWrite")
    public boolean canWrite() {
        return canWrite;
    }

    @JsonGetter("exists")
    public boolean exists() {
        return exists;
    }

    @JsonGetter("absolutePath")
    public String getAbsolutePath() {
        return absolutePath;
    }

    @JsonGetter("canonicalPath")
    public String getCanonicalPath() {
        return canonicalPath;
    }

    @JsonGetter("freeSpace")
    public long getFreeSpace() {
        return freeSpace;
    }

    @JsonGetter("name")
    public String getName() {
        return name;
    }

    @JsonGetter("parent")
    public String getParent() {
        return parent;
    }

    @JsonGetter("path")
    public String getPath() {
        return path;
    }

    @JsonGetter("totalSpace")
    public long getTotalSpace() {
        return totalSpace;
    }

    @JsonGetter("usableSpace")
    public long getUsableSpace() {
        return usableSpace;
    }

    @JsonGetter("isAbsolute")
    public boolean isAbsolute() {
        return isAbsolute;
    }

    @JsonGetter("isDirectory")
    public boolean isDirectory() {
        return isDirectory;
    }

    @JsonGetter("isFile")
    public boolean isFile() {
        return isFile;
    }

    @JsonGetter("isHidden")
    public boolean isHidden() {
        return isHidden;
    }

    @JsonGetter("lastModified")
    public long lastModified() {
        return lastModified;
    }

    @JsonGetter("length")
    public long length() {
        return length;
    }

    public ResultTask<Boolean> createNewFile() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("createNewFile", Boolean.class)).postMethodProcess(context);
    }

    @Annotations.StaticMethod
    public ResultTask<File> createTempFile(String prefix, String suffix) {
        return new ResultCreator<File>(buildMethodCallPacketData("createTempFile", File.class, prefix, suffix)).postMethodProcess(context);
    }

    public ResultTask<Boolean> delete() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("delete", Boolean.class)).postMethodProcess(context);
    }

    public ResultTask<String[]> list() {
        return new ResultCreator<String[]>(buildMethodCallPacketData("list", String[].class)).postMethodProcess(context);
    }

    public ResultTask<String[]> list(NameFilters.NameFilter<String> filters) {
        ResultTask<String[]> finalResult = new ResultTask<>();
        ResultTask<String[]> rawResult = new ResultCreator<String[]>(buildMethodCallPacketData("list", String[].class))
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
        return invokeListFiles("listFiles",null, null);
    }

    public ResultTask<File[]> listFiles(NameFilters.NameFilter<String> filters) {
        return invokeListFiles("listFiles", filters, null);
    }

    public ResultTask<File[]> listFiles(NameFilters.NameFilterWithExtra<String, File> filters) {
        return invokeListFiles("listFiles",null, filters);
    }

    @Annotations.StaticMethod
    public ResultTask<File[]> listRoots() {
        return invokeListFiles("listRoots", null, null);
    }

    private ResultTask<File[]> invokeListFiles(String methodName,
                                               @Nullable NameFilters.NameFilter<String> filterWithName,
                                               @Nullable NameFilters.NameFilterWithExtra<String, File> filterWithObj) {
        ResultTask<File[]> finalResult = new ResultTask<>();
        ResultTask<FileModel[]> rawResult = new ResultCreator<FileModel[]>(buildMethodCallPacketData(methodName, FileModel[].class))
                .postMethodProcess(context);

        rawResult.setOnTaskCompleteListener(result -> {
            ResultTask.Result<File[]> resultObj = new ResultTask.Result<>();
            resultObj.setSuccess(result.isSuccess());
            resultObj.setHasException(result.hasException());
            resultObj.setException(result.getException());

            if(result.isSuccess()) {
                ArrayList<File> resultList = new ArrayList<>();
                for(FileModel files : (FileModel[]) result.getResultData()) {
                    File convertedFileObj = (File) ServiceProcess.convertToFinalFormat(files, File.class);
                    convertedFileObj.context = context;
                    convertedFileObj.setIsFetched(true);

                    if(filterWithName != null && filterWithName.accept(convertedFileObj.name)) {
                        resultList.add(convertedFileObj);
                    } else if(filterWithObj != null && filterWithObj.accept(convertedFileObj.name, convertedFileObj)) {
                        resultList.add(convertedFileObj);
                    } else {
                        resultList.add(convertedFileObj);
                    }
                }
                resultObj.setResultData(resultList.toArray());
            }
            finalResult.callCompleteListener(resultObj);
        });

        finalResult.mOnInvokeAttached = result1 -> rawResult.invokeTask();
        return finalResult;
    }

    public ResultTask<Boolean> mkdir() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("mkdir", Boolean.class)).postMethodProcess(context);
    }

    public ResultTask<Boolean> mkdirs() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("mkdirs", Boolean.class)).postMethodProcess(context);
    }

    public ResultTask<Boolean> renameTo(File dest) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("renameTo", Boolean.class, dest)).postMethodProcess(context);
    }

    public ResultTask<Boolean> setExecutable(boolean executable) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("setExecutable", Boolean.class, executable)).postMethodProcess(context);
    }

    public ResultTask<Boolean> setLastModified(long time) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("setExecutable", Boolean.class, time)).postMethodProcess(context);
    }

    public ResultTask<Boolean> setReadOnly() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("setReadOnly", Boolean.class)).postMethodProcess(context);
    }

    public ResultTask<Boolean> setReadable(boolean readable) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("setReadable", Boolean.class, readable)).postMethodProcess(context);
    }

    public ResultTask<Boolean> setWritable(boolean writable) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("setWritable", Boolean.class, writable)).postMethodProcess(context);
    }

    public ResultTask<URI> toURI() {
        return new ResultCreator<URI>(buildMethodCallPacketData("toURI", URI.class)).postMethodProcess(context);
    }

    public ResultTask<InputStream> openFileInputStream() {
        return new ResultCreator<InputStream>(buildStreamCallPacketData(true), true).postMethodProcess(context);
    }

    public ResultTask<OutputStream> openFileOutputStream() {
        return new ResultCreator<OutputStream>(buildStreamCallPacketData(false), true).postMethodProcess(context);
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

    private PacketData buildMethodCallPacketData(String methodName, Class<?> parameterCls, Object... args) {
        Instance instance = Instance.getInstance();
        PacketData packet = new PacketData();
        ArgsInfo argsInfo = new ArgsInfo();
        String ticket = String.format("%s@%s", methodName, System.currentTimeMillis());

        argsInfo.put(getAbsolutePath());
        argsInfo.put(parameterCls, methodName);

        for(Object arg : args) {
            argsInfo.put(arg);
        }

        packet.argsInfo = argsInfo;
        packet.fromPackageName = instance.getServerPeer().getPackageName();
        packet.ticketId = ticket;
        packet.apiType = ProcessConst.ACTION_TYPE_FILE;
        packet.actionType = ProcessConst.ACTION_REQUEST_METHOD;

        return packet;
    }

    private PacketData buildStreamCallPacketData(Boolean isInputStream) {
        Instance instance = Instance.getInstance();
        PacketData packet = new PacketData();
        ArgsInfo argsInfo = new ArgsInfo();

        String ticket = String.format(ProcessConst.STREAM_KEY_PREFIX,
                isInputStream ? ProcessConst.STREAM_INPUT : ProcessConst.STREAM_OUTPUT,
                System.currentTimeMillis());

        argsInfo.put(getAbsolutePath());
        argsInfo.put(isInputStream);

        packet.argsInfo = argsInfo;
        packet.fromPackageName = instance.getServerPeer().getPackageName();
        packet.ticketId = ticket;
        packet.apiType = ProcessConst.ACTION_TYPE_FILE;
        packet.actionType = ProcessConst.ACTION_REQUEST_STREAM;

        return packet;
    }
}

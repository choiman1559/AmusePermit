package com.amuse.permit.wrapper.file;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.model.ServiceProcess;
import com.amuse.permit.process.ProcessConst;
import com.amuse.permit.process.ProcessRoute;
import com.amuse.permit.process.ProcessStream;
import com.amuse.permit.process.action.ServerAction;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

public class FileProcessor extends ServiceProcess {

    @Override
    public void onClassRequested(Context context) throws Exception {
        PacketData packetData = getPacketData();
        FileModel model = ((FileModel) ((FileModel) getNativeImplClass().newInstance()).createServerInstance(packetData.argsInfo));
        model.setIsFetched(true);

        new ServerAction(context, packetData)
                .pushClass(getType(), ServiceProcess.convertToFinalFormat(model, FileModel.class))
                .send();
    }

    @Override
    public void onClassResponded(Context context, Bundle bundle) {
        PacketData packetData = getPacketData();
        FileModel fileNativeWrapper = (FileModel) bundle.getSerializable(ProcessConst.KEY_EXTRA_DATA);
        ResultTask.Result<File> fileClsResult = new ResultTask.Result<>();
        fileClsResult.setHasException(false);

        if(fileNativeWrapper != null) {
            File file = (File) convertToFinalFormat(fileNativeWrapper, File.class);
            file.context = ProcessRoute.getInnerResultObjContext(packetData.ticketId);

            fileClsResult.setSuccess(true);
            file.setIsFetched(true);
            fileClsResult.setResultData(file);
        } else {
            fileClsResult.setSuccess(false);
            fileClsResult.setResultData(null);
        }
        ProcessRoute.callInnerResultTask(packetData.ticketId, fileClsResult);
    }

    @Override
    public void onMethodRequested(Context context) throws Exception {
        PacketData packetData = getPacketData();
        FileModel fileNativeWrapper = ((FileModel) ((FileModel) getNativeImplClass().newInstance()).createServerInstance(packetData.argsInfo));

        Class<?>[] clsArr = new Class<?>[packetData.argsInfo.size() - 2];
        Object[] argsArr = new Object[packetData.argsInfo.size() - 2];

        for(int i = 2; i < clsArr.length + 1; i++) {
            clsArr[i] = packetData.argsInfo.getCls(i);
            argsArr[i] = packetData.argsInfo.getData(i);
        }

        Object resultObj;
        if(clsArr.length > 0) {
            Method method = getNativeImplClass().getDeclaredMethod((String) packetData.argsInfo.getData(1), clsArr);
            resultObj = method.invoke(fileNativeWrapper, argsArr);
        } else {
            Method method = getNativeImplClass().getDeclaredMethod((String) packetData.argsInfo.getData(1));
            resultObj = method.invoke(fileNativeWrapper);
        }

        ArgsInfo resultArgs = new ArgsInfo();
        resultArgs.put(packetData.argsInfo.getCls(1), resultObj);

        new ServerAction(context, packetData)
                .pushMethod(getType(), resultArgs)
                .send();
    }

    @Override
    public void onMethodResponded(Context context, Bundle bundle) {
        PacketData packetData = getPacketData();
        ResultTask.Result<?> methodResult = new ResultTask.Result<>();
        Object methodResultObj = packetData.argsInfo.getData(0);

        methodResult.setSuccess(methodResultObj != null);
        methodResult.setResultData(packetData.argsInfo.getCls(0).cast(methodResultObj));
        methodResult.setHasException(false);

        ProcessRoute.callInnerResultTask(packetData.ticketId, methodResult);
    }

    @Override
    public void onStreamRequested(Context context) throws Exception {
        PacketData packetData = getPacketData();
        java.io.File baseFile = new java.io.File((String) packetData.argsInfo.getData(0));
        Boolean isInputStream = (Boolean) packetData.argsInfo.getData(1);

        if(isInputStream) {
            ProcessStream.inputStreamMap.put(packetData.ticketId, new FileInputStream(baseFile));
        } else {
            ProcessStream.outputStreamMap.put(packetData.ticketId, new FileOutputStream(baseFile));
        }
        new ServerAction(context, packetData)
                .pushStream(getType(), packetData.argsInfo)
                .send();
    }

    @Override
    public void onStreamResponded(Context context, Bundle bundle) throws Exception {
        PacketData packetData = getPacketData();
        Uri fileUri = Uri.parse(String.format("content://%s$%s/%s", ProcessConst.PACKAGE_STREAM, packetData.fromPackageName, packetData.ticketId));
        ContentResolver contentResolver = context.getContentResolver();
        Boolean isInputStream = (Boolean) packetData.argsInfo.getData(1);

        if(isInputStream) {
            ResultTask.Result<InputStream> inputStreamResult = new ResultTask.Result<>();
            inputStreamResult.setResultData(contentResolver.openInputStream(fileUri));
            inputStreamResult.setSuccess(true);
            inputStreamResult.setHasException(false);
            ProcessRoute.callInnerResultTask(packetData.ticketId, inputStreamResult);
        } else {
            ResultTask.Result<OutputStream> outputStreamResult = new ResultTask.Result<>();
            outputStreamResult.setResultData(contentResolver.openOutputStream(fileUri));
            outputStreamResult.setSuccess(true);
            outputStreamResult.setHasException(false);
            ProcessRoute.callInnerResultTask(packetData.ticketId, outputStreamResult);
        }
    }

    @Override
    public String getType() {
        return ProcessConst.ACTION_TYPE_FILE;
    }

    @Override
    public Class<?> getNativeImplClass() {
        try {
            return Class.forName(String.format("%s.wrapper.%s.FileNativeWrapper", ProcessConst.PACKAGE_MODULE, getType()));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}

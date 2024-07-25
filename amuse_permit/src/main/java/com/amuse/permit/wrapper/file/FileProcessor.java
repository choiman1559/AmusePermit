package com.amuse.permit.wrapper.file;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.amuse.permit.Instance;
import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.model.ServiceProcess;
import com.amuse.permit.process.ProcessConst;
import com.amuse.permit.process.ProcessRoute;
import com.amuse.permit.process.ProcessStream;
import com.amuse.permit.process.action.ServerAction;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

public class FileProcessor extends ServiceProcess {

    @SuppressLint("Recycle")
    @Override
    public void onPacketReceived(Context context, Bundle bundle) throws Exception {
        super.onPacketReceived(context, bundle);
        PacketData packetData = getPacketData();
        Instance instance = Instance.getInstance();

        if(instance.hasModFlag(Instance.OPERATE_MODE_SERVER)) {
            String filePath = (String) packetData.argsInfo.getData(0);
            java.io.File baseFile = new java.io.File(filePath);

            switch (packetData.actionType) {
                case ProcessConst.ACTION_REQUEST_CLASS:
                    new ServerAction(context, packetData)
                            .pushClass(getType(), new FileNativeWrapper(baseFile))
                            .send();
                    break;

                case ProcessConst.ACTION_REQUEST_METHOD:
                    FileNativeWrapper fileNativeWrapper = new FileNativeWrapper(baseFile);

                    Class<?>[] clsArr = new Class<?>[packetData.argsInfo.size()];
                    Object[] argsArr = new Object[packetData.argsInfo.size()];

                    for(int i = 2; i < packetData.argsInfo.size(); i++) {
                        clsArr[i] = packetData.argsInfo.getCls(i);
                        argsArr[i] = packetData.argsInfo.getData(i);
                    }

                    Method method = getNativeImplClass().getDeclaredMethod((String) packetData.argsInfo.getData(1), clsArr);
                    Object resultObj = method.invoke(fileNativeWrapper, argsArr);

                    ArgsInfo resultArgs = new ArgsInfo();
                    resultArgs.put(packetData.argsInfo.getCls(1), resultObj);

                    new ServerAction(context, packetData)
                            .pushMethod(getType(), resultArgs)
                            .send();
                    break;

                case ProcessConst.ACTION_REQUEST_MEMBER:
                    // UNUSED API FOR THIS WRAPPER CONTEXT
                    UnsupportedOperationException exception = new UnsupportedOperationException("ACTION_REQUEST_MEMBER is not supported on File wrapper");
                    new ServerAction(context, packetData)
                            .setException(exception)
                            .send();
                    break;

                case ProcessConst.ACTION_REQUEST_STREAM:
                    Boolean isInputStream = (Boolean) packetData.argsInfo.getData(1);
                    if(isInputStream) {
                        ProcessStream.inputStreamMap.put(packetData.ticketId, new FileInputStream(baseFile));
                    } else {
                        ProcessStream.outputStreamMap.put(packetData.ticketId, new FileOutputStream(baseFile));
                    }
                    new ServerAction(context, packetData)
                            .pushStream(getType(), packetData.argsInfo)
                            .send();
                    break;
            }
        }

        if(instance.hasModFlag(Instance.OPERATE_MODE_CLIENT)) {
            switch (packetData.actionType) {
                case ProcessConst.ACTION_RESPONSE_CLASS:
                    FileNativeWrapper fileNativeWrapper = (FileNativeWrapper) bundle.getSerializable(ProcessConst.KEY_EXTRA_DATA);

                    ResultTask.Result<File> fileClsResult = new ResultTask.Result<>();
                    fileClsResult.setHasException(false);

                    if(fileNativeWrapper != null) {
                        File file = new ObjectMapper().setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
                                .convertValue(fileNativeWrapper, File.class);
                        fileClsResult.setSuccess(true);
                        file.setIsFetched(true);
                        fileClsResult.setResultData(file);
                    } else {
                        fileClsResult.setSuccess(false);
                        fileClsResult.setResultData(null);
                    }
                    ProcessRoute.callInnerResultTask(packetData.ticketId, fileClsResult);
                    break;

                case ProcessConst.ACTION_RESPONSE_METHOD:
                    ResultTask.Result<?> methodResult = new ResultTask.Result<>();
                    Object methodResultObj = packetData.argsInfo.getData(0);

                    methodResult.setSuccess(methodResultObj != null);
                    methodResult.setResultData(packetData.argsInfo.getCls(0).cast(methodResultObj));
                    methodResult.setHasException(false);

                    ProcessRoute.callInnerResultTask(packetData.ticketId, methodResult);
                    break;

                case ProcessConst.ACTION_RESPONSE_MEMBER:
                    // UNUSED API FOR THIS WRAPPER CONTEXT
                    UnsupportedOperationException exception = new UnsupportedOperationException("ACTION_REQUEST_MEMBER is not supported on File wrapper");
                    ResultTask.Result<Object> unsuppoertedResult = new ResultTask.Result<>();

                    unsuppoertedResult.setResultData(null);
                    unsuppoertedResult.setSuccess(false);
                    unsuppoertedResult.setHasException(true);
                    unsuppoertedResult.setException(exception);
                    ProcessRoute.callInnerResultTask(packetData.ticketId, unsuppoertedResult);
                    break;

                case ProcessConst.ACTION_RESPONSE_STREAM:
                    Uri fileUri = Uri.parse("content://" + ProcessConst.PACKAGE_STREAM + "/" + packetData.ticketId);
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
                    break;
            }
        }
    }

    @Override
    public String getType() {
        return ProcessConst.ACTION_TYPE_FILE;
    }

    @Override
    public Class<?> getNativeImplClass() {
        return FileNativeWrapper.class;
    }
}

package com.amuse.permit.wrapper.locate;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;

import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.model.Annotations;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.model.Wrappable;
import com.amuse.permit.process.ProcessRoute;
import com.amuse.permit.process.ServiceProcess;
import com.amuse.permit.process.ProcessConst;
import com.amuse.permit.process.action.ServerAction;

import java.lang.reflect.Method;

public class LocateProcessor extends ServiceProcess {

    @Override
    public void onMethodRequested(Context context) throws Exception {
        PacketData packetData = getPacketData();
        ArgsInfo argsInfo = packetData.argsInfo;

        LocateModel preProcesslocateModel = new LocateModel();
        preProcesslocateModel.mockLocation = (Location) packetData.parcelableList.get(0);
        preProcesslocateModel.isMock = (boolean) argsInfo.getData(1);
        argsInfo.set(0, LocateModel.class, preProcesslocateModel);

        ResultTask<Wrappable> nativeModelTask = ((LocateModel) getNativeImplClass().newInstance()).createServerInstance(context, argsInfo);

        nativeModelTask.setOnTaskCompleteListener(result -> {
            try {
                if(result.isSuccess()) {
                    LocateModel locateModel = (LocateModel) result.getResultData();
                    Class<?>[] clsArr = new Class<?>[packetData.argsInfo.size() - 3];
                    Object[] argsArr = new Object[packetData.argsInfo.size() - 3];

                    for(int i = 0; i < clsArr.length; i++) {
                        clsArr[i] = packetData.argsInfo.getCls(i + 3);
                        argsArr[i] = packetData.argsInfo.getData(i + 3);

                        if(argsArr[i].equals(ProcessConst.KEY_PARCEL_REPLACED)) {
                            argsArr[i] = packetData.parcelableList.get(i + 3);
                        }
                    }

                    ResultTask<?> resultObj;
                    if(clsArr.length > 0) {
                        Method method = getNativeImplClass().getDeclaredMethod((String) packetData.argsInfo.getData(2), clsArr);
                        resultObj = (ResultTask<?>) method.invoke(locateModel, argsArr);
                    } else {
                        Method method = getNativeImplClass().getDeclaredMethod((String) packetData.argsInfo.getData(2));
                        resultObj = (ResultTask<?>) method.invoke(locateModel);
                    }

                    if(resultObj != null) {
                        resultObj.setOnTaskCompleteListener(result1 -> {
                            if(result1.isSuccess()) {
                                Object object = result1.getResultData();
                                if(object instanceof Parcelable) {
                                    new ServerAction(context, packetData)
                                            .pushMethod(getType(), (Parcelable) object)
                                            .send();
                                } else {
                                    ArgsInfo resultArgs = new ArgsInfo();
                                    resultArgs.put(packetData.argsInfo.getCls(1), object);

                                    new ServerAction(context, packetData)
                                            .pushMethod(getType(), resultArgs)
                                            .send();
                                }
                            } else {
                                if(result1.hasException()) {
                                    ProcessRoute.sendException(context, packetData, result1.getException());
                                } else {
                                    ProcessRoute.sendException(context, packetData, new IllegalStateException());
                                }
                            }
                        }).invokeTask();
                    } else {
                        ProcessRoute.sendException(context, packetData, new NullPointerException());
                    }
                } else {
                    if(result.hasException()) {
                        ProcessRoute.sendException(context, packetData, result.getException());
                    } else {
                        ProcessRoute.sendException(context, packetData, new IllegalStateException());
                    }
                }
            } catch (Exception e) {
                ProcessRoute.sendException(context, packetData, e);
            }
        }).invokeTask();
    }

    @Override
    public void onMethodResponded(Context context, Bundle bundle) {
        PacketData packetData = getPacketData();
        ResultTask.Result<?> methodResult = new ResultTask.Result<>();
        Object methodResultObj = packetData.argsInfo.getData(0);

        if(methodResultObj == null || methodResultObj.equals(ProcessConst.KEY_PARCEL_REPLACED)) {
            methodResultObj = bundle.getParcelable(ProcessConst.KEY_EXTRA_PARCEL_DATA);
        }

        methodResult.setSuccess(methodResultObj != null);
        methodResult.setResultData(packetData.argsInfo.getCls(0).cast(methodResultObj));
        methodResult.setHasException(false);

        ProcessRoute.callInnerResultTask(packetData.ticketId, methodResult);
    }

    @Override
    public @Annotations.ApiTypes String getType() {
        return ProcessConst.ACTION_TYPE_LOCATION;
    }

    @Override
    public Class<?> getNativeImplClass() {
        return getDefaultNativeClass("Locate");
    }
}

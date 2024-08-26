package com.amuse.permit.wrapper.pkg;

import android.content.Context;
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
import java.util.ArrayList;

public class PackageProcess extends ServiceProcess {

    @SuppressWarnings("unchecked")
    @Override
    public void onMethodRequested(Context context) throws Exception {
        PacketData packetData = getPacketData();
        ArgsInfo argsInfo = packetData.argsInfo;
        ResultTask<Wrappable> nativeModelTask = ((QueryPkgModel) getNativeImplClass().newInstance()).createServerInstance(context, argsInfo);

        nativeModelTask.setOnTaskCompleteListener(result -> {
            try {
                if(result.isSuccess()) {
                    final int methodMetaDataIndex = 0;
                    final int methodMetaCorrelationIndex = methodMetaDataIndex + 1;
                    int parcelableCount = 0;

                    QueryPkgModel locateModel = (QueryPkgModel) result.getResultData();
                    Class<?>[] clsArr = new Class<?>[packetData.argsInfo.size() - methodMetaCorrelationIndex];
                    Object[] argsArr = new Object[packetData.argsInfo.size() - methodMetaCorrelationIndex];

                    for(int i = 0; i < clsArr.length; i++) {
                        clsArr[i] = packetData.argsInfo.getCls(i + methodMetaCorrelationIndex);
                        argsArr[i] = packetData.argsInfo.getData(i + methodMetaCorrelationIndex);

                        if(argsArr[i].equals(ProcessConst.KEY_PARCEL_REPLACED)) {
                            argsArr[i] = packetData.parcelableList.get(parcelableCount);
                            parcelableCount += 1;
                        }
                    }

                    Object resultObj;
                    if(clsArr.length > 0) {
                        Method method = getNativeImplClass().getDeclaredMethod((String) packetData.argsInfo.getData(methodMetaDataIndex), clsArr);
                        resultObj = method.invoke(locateModel, argsArr);
                    } else {
                        Method method = getNativeImplClass().getDeclaredMethod((String) packetData.argsInfo.getData(methodMetaDataIndex));
                        resultObj = method.invoke(locateModel);
                    }

                    if(resultObj instanceof ArrayList) {
                        new ServerAction(context, packetData)
                                .pushMethod(getType(), (ArrayList<Parcelable>) resultObj)
                                .send();
                    } else if(resultObj instanceof Parcelable) {
                        new ServerAction(context, packetData)
                                .pushMethod(getType(), (Parcelable) resultObj)
                                .send();
                    } else {
                        ArgsInfo resultArgs = new ArgsInfo();
                        resultArgs.put(packetData.argsInfo.getCls(methodMetaDataIndex), resultObj);

                        new ServerAction(context, packetData)
                                .pushMethod(getType(), resultArgs)
                                .send();
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

        if (methodResultObj.equals(ProcessConst.KEY_PARCEL_REPLACED)) {
            methodResultObj = bundle.getParcelable(ProcessConst.KEY_EXTRA_PARCEL_DATA);
        } else if (methodResultObj.equals(ProcessConst.KEY_PARCEL_LIST_REPLACED)) {
            methodResultObj = bundle.getParcelableArrayList(ProcessConst.KEY_EXTRA_PARCEL_LIST_DATA);
        }

        if(packetData.argsInfo.getCls(0) == Void.class) {
            methodResult.setSuccess(true);
        } else {
            methodResult.setSuccess(methodResultObj != null);
            methodResult.setResultData(packetData.argsInfo.getCls(0).cast(methodResultObj));
        }

        methodResult.setHasException(false);
        ProcessRoute.callInnerResultTask(packetData.ticketId, methodResult);
    }

    @Override
    public @Annotations.ApiTypes String getType() {
        return ProcessConst.ACTION_TYPE_PACKAGE;
    }

    @Override
    public void onPacketReceived(Context context, Bundle bundle) throws Exception {
        super.onPacketReceived(context, bundle);
    }

    @Override
    public Class<?> getNativeImplClass() {
        return getDefaultNativeClass("QueryPkg");
    }
}

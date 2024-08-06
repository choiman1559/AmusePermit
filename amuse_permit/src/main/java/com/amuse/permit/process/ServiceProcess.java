package com.amuse.permit.process;

import android.content.Context;
import android.os.Bundle;

import com.amuse.permit.Instance;
import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.model.Processable;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.model.Wrappable;
import com.amuse.permit.process.action.ServerAction;
import com.amuse.permit.wrapper.file.File;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;

@SuppressWarnings("unused")
public abstract class ServiceProcess implements Processable {

    PacketData packetData;

    @Override
    public void onPacketReceived(Context context, Bundle bundle) throws Exception {
        packetData = new PacketData(bundle);
        if(packetData.actionType == null) {
            throw new IllegalStateException("Action type must not be Null");
        }

        PacketData packetData = getPacketData();
        Instance instance = Instance.getInstance();

        if(instance.hasModFlag(Instance.OPERATE_MODE_SERVER)) {
            switch (packetData.actionType) {
                case ProcessConst.ACTION_REQUEST_CLASS:
                    onClassRequested(context);
                    break;

                case ProcessConst.ACTION_REQUEST_METHOD:
                    onMethodRequested(context);
                    break;

                case ProcessConst.ACTION_REQUEST_MEMBER:
                    onMemberRequested(context);
                    break;

                case ProcessConst.ACTION_REQUEST_STREAM:
                    onStreamRequested(context);
                    break;
            }
        }

        if(instance.hasModFlag(Instance.OPERATE_MODE_CLIENT)) {
            switch (packetData.actionType) {
                case ProcessConst.ACTION_RESPONSE_CLASS:
                    onClassResponded(context, bundle);
                    break;

                case ProcessConst.ACTION_RESPONSE_METHOD:
                    onMethodResponded(context, bundle);
                    break;

                case ProcessConst.ACTION_RESPONSE_MEMBER:
                    onMemberResponded(context, bundle);
                    break;

                case ProcessConst.ACTION_RESPONSE_STREAM:
                    onStreamResponded(context, bundle);
                    break;
            }
        }
    }

    public void onClassRequested(Context context) throws Exception {
        Wrappable model = (((Wrappable) getNativeImplClass().newInstance()).createServerInstance(packetData.argsInfo));
        checkWrappablePermissionGranted(context, model);
        model.setIsFetched(true);

        new ServerAction(context, packetData)
                .pushClass(getType(), model)
                .send();
    }

    public void onMethodRequested(Context context) throws Exception {
        Wrappable nativeWrapper = (((Wrappable) getNativeImplClass().newInstance()).createServerInstance(packetData.argsInfo));
        checkWrappablePermissionGranted(context, nativeWrapper);

        Class<?>[] clsArr = new Class<?>[packetData.argsInfo.size() - 2];
        Object[] argsArr = new Object[packetData.argsInfo.size() - 2];

        for(int i = 2; i < clsArr.length + 1; i++) {
            clsArr[i] = packetData.argsInfo.getCls(i);
            argsArr[i] = packetData.argsInfo.getData(i);
        }

        Object resultObj;
        if(clsArr.length > 0) {
            Method method = getNativeImplClass().getDeclaredMethod((String) packetData.argsInfo.getData(1), clsArr);
            resultObj = method.invoke(nativeWrapper, argsArr);
        } else {
            Method method = getNativeImplClass().getDeclaredMethod((String) packetData.argsInfo.getData(1));
            resultObj = method.invoke(nativeWrapper);
        }

        ArgsInfo resultArgs = new ArgsInfo();
        resultArgs.put(packetData.argsInfo.getCls(1), resultObj);

        new ServerAction(context, packetData)
                .pushMethod(getType(), resultArgs)
                .send();
    }

    public void onMemberRequested(Context context) throws Exception {
        // NEED TO IMPLEMENT MANUALLY THIS FEATURE
        throw new UnsupportedOperationException("ACTION_REQUEST_MEMBER is not implemented on default ServiceProcessor");
    }

    public void onStreamRequested(Context context) throws Exception {
        // NEED TO IMPLEMENT MANUALLY THIS FEATURE
        throw new UnsupportedOperationException("ACTION_REQUEST_STREAM is not implemented on default ServiceProcessor");
    }

    public void onClassResponded(Context context, Bundle bundle) throws Exception {
        Wrappable nativeWrapper = (Wrappable) bundle.getSerializable(ProcessConst.KEY_EXTRA_DATA);
        ResultTask.Result<File> clsResult = new ResultTask.Result<>();
        clsResult.setHasException(false);

        if(nativeWrapper != null) {
            Wrappable resultObj = convertToFinalFormat(nativeWrapper, Wrappable.class);
            clsResult.setSuccess(true);
            resultObj.setIsFetched(true);
            clsResult.setResultData(resultObj);
        } else {
            clsResult.setSuccess(false);
            clsResult.setResultData(null);
        }
        ProcessRoute.callInnerResultTask(packetData.ticketId, clsResult);
    }

    public void onMethodResponded(Context context, Bundle bundle) throws Exception {
        ResultTask.Result<?> methodResult = new ResultTask.Result<>();
        Object methodResultObj = packetData.argsInfo.getData(0);

        methodResult.setSuccess(methodResultObj != null);
        methodResult.setResultData(packetData.argsInfo.getCls(0).cast(methodResultObj));
        methodResult.setHasException(false);

        ProcessRoute.callInnerResultTask(packetData.ticketId, methodResult);
    }

    public void onMemberResponded(Context context, Bundle bundle) throws Exception {
        // UNUSED API FOR THIS WRAPPER CONTEXT
        UnsupportedOperationException exception = new UnsupportedOperationException("ACTION_RESPONSE_MEMBER is not implemented on default ServiceProcessor");
        ResultTask.Result<Object> unsuppoertedResult = new ResultTask.Result<>();

        unsuppoertedResult.setResultData(null);
        unsuppoertedResult.setSuccess(false);
        unsuppoertedResult.setHasException(true);
        unsuppoertedResult.setException(exception);
        ProcessRoute.callInnerResultTask(packetData.ticketId, unsuppoertedResult);
    }

    public void onStreamResponded(Context context, Bundle bundle) throws Exception {
        // UNUSED API FOR THIS WRAPPER CONTEXT
        UnsupportedOperationException exception = new UnsupportedOperationException("ACTION_RESPONSE_STREAM is not implemented on default ServiceProcessor");
        ResultTask.Result<Object> unsuppoertedResult = new ResultTask.Result<>();

        unsuppoertedResult.setResultData(null);
        unsuppoertedResult.setSuccess(false);
        unsuppoertedResult.setHasException(true);
        unsuppoertedResult.setException(exception);
        ProcessRoute.callInnerResultTask(packetData.ticketId, unsuppoertedResult);
    }

    public static Wrappable convertToFinalFormat(Wrappable wrapper, Class<?> targetClass) {
        InjectableValues.Std injectableValues = new InjectableValues.Std();
        injectableValues.addValue("isFetched", true);

        return (Wrappable) new ObjectMapper()
                .setInjectableValues(injectableValues)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
                .convertValue(wrapper, targetClass);
    }

    public PacketData getPacketData() {
        return packetData;
    }

    public void checkWrappablePermissionGranted(Context context, Wrappable wrappable) throws Exception{
        if(!wrappable.checkPermissionGranted(context)) {
            throw new IllegalStateException("Wrappable permission is not granted");
        }
    }
}

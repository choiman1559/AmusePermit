package com.amuse.permit.wrapper.cursor;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.model.Annotations;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.process.ProcessConst;
import com.amuse.permit.process.ProcessRoute;
import com.amuse.permit.process.ProcessStream;
import com.amuse.permit.process.ServiceProcess;
import com.amuse.permit.process.action.ServerAction;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class CursorProcess extends ServiceProcess {

    @Annotations.RequesterSide
    protected static ConcurrentHashMap<String, ContentObserver> contentObserverHashMap;
    @Annotations.ResponserSide
    protected static ConcurrentHashMap<String, ObserverWrapper> observerWrapperHashMap;

    protected static void registerOrRemoveContentObserver(boolean isRegister, ContentObserver contentObserver) {
        if(contentObserverHashMap == null) {
            contentObserverHashMap = new ConcurrentHashMap<>();
        }
        if(isRegister) {
            contentObserverHashMap.put(contentObserver.ticketId, contentObserver);
        } else {
            contentObserverHashMap.remove(contentObserver.ticketId);
        }
    }

    @Override
    public void onStreamRequested(Context context) {
        PacketData packetData = getPacketData();
        ArgsInfo argsInfo = new ArgsInfo();

        switch ((String) packetData.argsInfo.getData(0)) {
            case ProviderManager.QUERY:
                Uri uri = (Uri) packetData.parcelableList.get(0);
                argsInfo.put(ProviderManager.QUERY);

                if(uri != null) {
                    ProcessStream.queryUriMap.put(packetData.ticketId, new Object[]{context, uri});
                    argsInfo.put(true);
                } else {
                    argsInfo.put(false);
                }

                new ServerAction(context, packetData)
                        .pushStream(getType(), argsInfo)
                        .send();
                break;

            case ProviderManager.REGISTER_OBSERVER:
                argsInfo.put(ProviderManager.UNREGISTER_OBSERVER);
                if(observerWrapperHashMap == null) {
                    observerWrapperHashMap = new ConcurrentHashMap<>();
                }

                if(observerWrapperHashMap.containsKey(packetData.ticketId)) {
                    argsInfo.put(false);
                } else {
                    argsInfo.put(true);
                    ObserverWrapper wrapper = new ObserverWrapper(context, new Handler(), packetData);
                    observerWrapperHashMap.put(packetData.ticketId, wrapper);
                    context.getContentResolver().registerContentObserver((Uri) packetData.parcelableList.get(0),
                            (Boolean) packetData.argsInfo.getData(2), wrapper);
                }

                new ServerAction(context, packetData)
                        .pushStream(getType(), argsInfo)
                        .send();
                break;

            case ProviderManager.UNREGISTER_OBSERVER:
                argsInfo.put(ProviderManager.UNREGISTER_OBSERVER);
                String observerKey = (String) packetData.argsInfo.getData(1);

                if(observerWrapperHashMap != null && observerWrapperHashMap.containsKey(observerKey)) {
                    context.getContentResolver().unregisterContentObserver(Objects.requireNonNull(observerWrapperHashMap.get(observerKey)));
                    observerWrapperHashMap.remove(observerKey);
                    argsInfo.put(true);
                } else {
                    argsInfo.put(false);
                }

                new ServerAction(context, packetData)
                        .pushStream(getType(), argsInfo)
                        .send();
                break;
        }
    }

    @Override
    public void onStreamResponded(Context context, Bundle bundle) {
        PacketData packetData = getPacketData();
        switch ((String) packetData.argsInfo.getData(0)) {
            case ProviderManager.QUERY:
                ResultTask.Result<Boolean> cursorUriRegisterResult = new ResultTask.Result<>();

                cursorUriRegisterResult.setSuccess((Boolean) packetData.argsInfo.getData(0));
                cursorUriRegisterResult.setHasException(false);
                ProcessRoute.callInnerResultTask(packetData.ticketId, cursorUriRegisterResult);
                break;

            case ProviderManager.REGISTER_OBSERVER:
            case ProviderManager.UNREGISTER_OBSERVER:
                ResultTask.Result<Void> observerRegisterResult = new ResultTask.Result<>();
                observerRegisterResult.setSuccess((Boolean) packetData.argsInfo.getData(1));
                observerRegisterResult.setHasException(false);
                ProcessRoute.callInnerResultTask(packetData.ticketId, observerRegisterResult);
                break;

            case ProviderManager.CALL_OBSERVER:
                if(contentObserverHashMap != null && contentObserverHashMap.containsKey(packetData.ticketId)) {
                    ContentObserver contentObserver = contentObserverHashMap.get(packetData.ticketId);
                    Boolean selfChange = (Boolean) packetData.argsInfo.getData(1);
                    Uri uri = (Uri) packetData.parcelableList.get(0);

                    if(contentObserver != null) {
                        contentObserver.onChange(selfChange, uri);
                        contentObserver.onChange(selfChange);
                    }
                }
                break;
        }
    }

    @Override
    public @Annotations.ApiTypes String getType() {
        return ProcessConst.ACTION_TYPE_CURSOR;
    }

    @Override
    public void onPacketReceived(Context context, Bundle bundle) throws Exception {
        super.onPacketReceived(context, bundle);
    }

    @Override
    public Class<?> getNativeImplClass() {
        return this.getClass();
    }
}

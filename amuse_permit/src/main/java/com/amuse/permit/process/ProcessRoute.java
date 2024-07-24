package com.amuse.permit.process;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.amuse.permit.Instance;
import com.amuse.permit.data.AppPeer;
import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.model.Processable;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.process.action.ServerAction;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessRoute extends BroadcastReceiver {

    public static ConcurrentHashMap<String, ResultTask<?>> resultTaskHashMap = new ConcurrentHashMap<>();

    public static void registerInnerResultTask(String ticket, ResultTask<?> resultTask) {
        resultTaskHashMap.put(ticket, resultTask);
    }

    public static void unregisterInnerResultTask(String ticket) {
        resultTaskHashMap.remove(ticket);
    }

    public static void callInnerResultTask(String ticket, ResultTask.Result<?> resultObj) {
        if(resultTaskHashMap.containsKey(ticket)) {
            ResultTask<?> resultTask = resultTaskHashMap.get(ticket);
            if(resultTask != null) {
                resultTask.callCompleteListener(resultObj);
            }
            unregisterInnerResultTask(ticket);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Instance instance = Instance.getInstance();
        String intentAction = intent.getAction();
        if(intentAction == null || !intentAction.equals(ProcessConst.PACKAGE_BROADCAST_ACTION)) {
            return;
        }

        Bundle bundle = intent.getExtras();
        if(bundle == null) {
            NullPointerException exception = new NullPointerException("Bundle extra KEY_TARGET must not be Null");
            String fromPackageName = intent.getStringExtra(ProcessConst.KEY_PACKAGE_NAME);
            String ticketId = intent.getStringExtra(ProcessConst.KEY_TICKET_ID);

            new ServerAction(context, fromPackageName, ticketId)
                    .setException(exception)
                    .send();
            return;
        }

        PacketData packetData = new PacketData(Objects.requireNonNull(bundle));

        if(packetData.actionType.equals(ProcessConst.ACTION_TYPE_HANDSHAKE)) {
            String actionType = intent.getStringExtra(ProcessConst.KEY_ACTION_TYPE);
            if(actionType != null) switch (actionType) {
                case ProcessConst.ACTION_REQUEST_HANDSHAKE:
                    new ServerAction(context, packetData)
                            .setHandShake()
                            .send();
                    break;

                case ProcessConst.ACTION_RESPONSE_HANDSHAKE:
                    ArgsInfo argsInfo = (ArgsInfo) intent.getSerializableExtra(ProcessConst.KEY_EXTRA_DATA);
                    ResultTask.Result<AppPeer> resultObj = new ResultTask.Result<>();
                    resultObj.setHasException(false);

                    if(argsInfo != null) {
                        AppPeer appPeer = new AppPeer(packetData.fromPackageName,
                                (Integer) argsInfo.getCls(0).cast(argsInfo.getData(0)),
                                (Boolean) argsInfo.getCls(1).cast(argsInfo.getData(1)),
                                (String[]) argsInfo.getCls(2).cast(argsInfo.getData(2)));
                        resultObj.setSuccess(true);
                        resultObj.setResultData(appPeer);
                    } else {
                        resultObj.setSuccess(false);
                    }

                    callInnerResultTask(packetData.ticketId, resultObj);
                    break;

                case ProcessConst.ACTION_RESPONSE_EXCEPTION:
                    ResultTask.Result<?> exceptionObj = new ResultTask.Result<>();
                    exceptionObj.setSuccess(false);
                    exceptionObj.setHasException(true);
                    exceptionObj.setException((Exception) intent.getSerializableExtra(ProcessConst.KEY_EXTRA_DATA));
                    callInnerResultTask(packetData.ticketId, exceptionObj);
                    break;
            }
            return;
        }

        if(!instance.packageNameFilter.accept(packetData.fromPackageName)) {
            IllegalAccessException exception = new IllegalAccessException("Package " + packetData.fromPackageName + " is not allowed in current scope");
            new ServerAction(context, packetData).setException(exception).send();
            return;
        }

        if(instance.processableMap.containsKey(packetData.actionType)) {
            Processable processable = instance.processableMap.get(packetData.actionType);
            if(processable != null) {
                try {
                    processable.onPacketReceived(context, intent.getExtras());
                } catch (Exception exception) {
                    new ServerAction(context, packetData)
                            .setException(exception)
                            .send();
                }
            } else {
                NullPointerException exception = new NullPointerException("Processable Service found but instance is null");
                new ServerAction(context, packetData)
                        .setException(exception)
                        .send();
            }
        } else {
            IllegalStateException exception = new IllegalStateException("Service type must not be Null or has not registered as Processable class");
            new ServerAction(context, packetData)
                    .setException(exception)
                    .send();
        }
    }
}

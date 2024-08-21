package com.amuse.permit.wrapper.cursor;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;

import com.amuse.permit.Instance;
import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.model.Annotations;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.model.Wrappable;
import com.amuse.permit.process.ProcessConst;
import com.amuse.permit.process.action.ResultCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("unused")
@Annotations.RequesterSide
public class ProviderManager extends Wrappable {

    @JsonIgnore
    private Context context;
    protected final static String QUERY = "query";
    protected final static String REGISTER_OBSERVER = "register_observer";
    protected final static String UNREGISTER_OBSERVER = "unregister_observer";
    protected final static String CALL_OBSERVER = "call_observer";

    @Annotations.Constructor
    public static ProviderManager getProviderManager(Context context) {
        ProviderManager providerManager = new ProviderManager();
        providerManager.context = context;
        return providerManager;
    }

    @SuppressLint("Recycle")
    public ResultTask<Cursor> query(Uri uri,
                                            String[] projection,
                                            String selection,
                                            String[] selectionArgs,
                                            String sortOrder) {

        ResultTask<Cursor> resultTask = new ResultTask<>();
        resultTask.mOnInvokeAttached = result -> {
            PacketData packetData = buildStreamCallPacketData(QUERY, Boolean.class, uri);
            ResultTask<Boolean> registerTask = new ResultCreator<Boolean>(packetData, true).postMethodProcess(context);

            registerTask.setOnTaskCompleteListener(registerTaskResult -> {
                ResultTask.Result<Cursor> cursorResult = new ResultTask.Result<>();
                cursorResult.setSuccess(registerTaskResult.isSuccess());

                Uri registeredUri = Uri.parse(String.format(ProcessConst.STREAM_AUTH_URI, ProcessConst.PACKAGE_STREAM, packetData.fromPackageName, packetData.ticketId));
                ContentResolver contentResolver = context.getContentResolver();
                cursorResult.setResultData(contentResolver.query(registeredUri, projection, selection, selectionArgs, sortOrder));
                resultTask.callCompleteListener(cursorResult);
            }).invokeTask();
        };
        return resultTask;
    }

    public ResultTask<Void> registerContentObserver(Uri uri, Boolean notifyForDescendants, ContentObserver registerContentObserver) {
        ResultTask<Void> resultTask = new ResultTask<>();
        resultTask.mOnInvokeAttached = result -> {
            ResultTask.Result<Void> cursorResult = new ResultTask.Result<>();
            PacketData packetData = buildStreamCallPacketData(REGISTER_OBSERVER, Void.class, uri, notifyForDescendants);
            if(registerContentObserver.ticketId != null && !registerContentObserver.ticketId.isEmpty()) {
                packetData.ticketId = registerContentObserver.ticketId;
            }

            ResultTask<Void> registerTask = new ResultCreator<Void>(packetData, true).postMethodProcess(context);
            registerTask.setOnTaskCompleteListener(registerTaskResult -> {
                cursorResult.setSuccess(registerTaskResult.isSuccess());
                registerContentObserver.ticketId = packetData.ticketId;
                CursorProcess.registerOrRemoveContentObserver(true, registerContentObserver);
                resultTask.callCompleteListener(cursorResult);
            }).invokeTask();
        };
        return resultTask;
    }

    public ResultTask<Void> unregisterContentObserver(ContentObserver unregisterContentObserver) {
        ResultTask<Void> resultTask = new ResultTask<>();
        resultTask.mOnInvokeAttached = result -> {
            ResultTask.Result<Void> cursorResult = new ResultTask.Result<>();
            if(unregisterContentObserver.ticketId == null || unregisterContentObserver.ticketId.isEmpty()) {
                cursorResult.setSuccess(false);
                cursorResult.setHasException(true);
                cursorResult.setException(new IllegalArgumentException("UnregisterContentObserver is not registered to Server"));
                resultTask.callCompleteListener(cursorResult);
                return;
            }

            PacketData packetData = buildStreamCallPacketData(UNREGISTER_OBSERVER, Void.class, unregisterContentObserver.ticketId);
            ResultTask<Void> registerTask = new ResultCreator<Void>(packetData, true).postMethodProcess(context);

            registerTask.setOnTaskCompleteListener(registerTaskResult -> {
                cursorResult.setSuccess(registerTaskResult.isSuccess());
                CursorProcess.registerOrRemoveContentObserver(false, unregisterContentObserver);
                resultTask.callCompleteListener(cursorResult);
            }).invokeTask();
        };
        return resultTask;
    }

    private PacketData buildStreamCallPacketData(String methodName, Class<?> parameterCls, Object... args) {
        Instance instance = Instance.getInstance();
        PacketData packet = new PacketData();
        String ticket = String.format("%s@%s", methodName, System.currentTimeMillis());

        ArgsInfo argsInfo = new ArgsInfo();
        argsInfo.put(parameterCls, methodName);

        for(Object arg : args) {
            if(arg instanceof Parcelable) {
                argsInfo.put(arg.getClass(), ProcessConst.KEY_PARCEL_REPLACED);
                packet.parcelableList.add((Parcelable) arg);
            } else {
                argsInfo.put(arg);
            }
        }

        packet.argsInfo = argsInfo;
        packet.fromPackageName = instance.getServerPeer().getPackageName();
        packet.ticketId = ticket;
        packet.apiType = ProcessConst.ACTION_TYPE_CURSOR;
        packet.actionType = ProcessConst.ACTION_REQUEST_STREAM;

        return packet;
    }
}

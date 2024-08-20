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
            PacketData packetData = buildStreamCallPacketData("query", Boolean.class, uri);
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

    @SuppressWarnings("SameParameterValue")
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

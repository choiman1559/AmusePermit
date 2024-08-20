package com.amuse.permit.wrapper.sms;

import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.amuse.permit.Instance;
import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.model.Annotations;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.process.ProcessConst;
import com.amuse.permit.process.action.ResultCreator;

import java.util.ArrayList;

@SuppressWarnings("unused")
@Annotations.RequesterSide
public class SmsManager extends SmsTokenModel {

    private final Context context;

    private SmsManager(Context context, @Nullable Integer subId) {
        this.context = context;
        this.smsSubscriptionId = subId;
    }

    public static SmsManager getDefaultSmsManager(Context context) {
        return new SmsManager(context, null);
    }

    public static SmsManager createForSubscriptionId(Context context, int subId) {
        return new SmsManager(context, subId);
    }

    @Nullable
    public Integer getSubscriptionId() {
        return smsSubscriptionId;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ResultTask<String> createAppSpecificSmsToken(PendingIntent intent) {
        return new ResultCreator<String>(buildMethodCallPacketData("createAppSpecificSmsToken", String.class,
                new Class[]{PendingIntent.class}, intent)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ResultTask<String> createAppSpecificSmsTokenWithPackageInfo(String prefixes, PendingIntent intent) {
        return new ResultCreator<String>(buildMethodCallPacketData("createAppSpecificSmsTokenWithPackageInfo", String.class,
                new Class[]{String.class, PendingIntent.class}, prefixes, intent)).postMethodProcess(context);
    }

    public ResultTask<ArrayList<String>> divideMessage(@NonNull String text) {
        return new ResultCreator<ArrayList<String>>(buildMethodCallPacketData("divideMessage", ArrayList.class, text)).postMethodProcess(context);
    }

    public ResultTask<Void> downloadMultimediaMessage(String locationUrl, Uri contentUri, Bundle configOverrides, PendingIntent downloadedIntent) {
        return new ResultCreator<Void>(buildMethodCallPacketData("downloadMultimediaMessage", Void.class,
                new Class[]{String.class, Uri.class, Bundle.class, PendingIntent.class},
                locationUrl, configOverrides, configOverrides, downloadedIntent)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public ResultTask<Void> downloadMultimediaMessage(String locationUrl, Uri contentUri, Bundle configOverrides, PendingIntent downloadedIntent, Long messageId) {
        return new ResultCreator<Void>(buildMethodCallPacketData("downloadMultimediaMessage", Void.class,
                new Class[]{String.class, Uri.class, Bundle.class, PendingIntent.class, Long.class},
                locationUrl, configOverrides, configOverrides, downloadedIntent, messageId)).postMethodProcess(context);
    }

    public ResultTask<Bundle> getCarrierConfigValues() {
        return new ResultCreator<Bundle>(buildMethodCallPacketData("getCarrierConfigValues", Bundle.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public ResultTask<Integer> getSmsCapacityOnIcc() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getSmsCapacityOnIcc", Integer.class)).postMethodProcess(context);
    }

    public ResultTask<Integer> getDefaultSmsSubscriptionId() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getDefaultSmsSubscriptionId", Integer.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<String> getSmscAddress() {
        return new ResultCreator<String>(buildMethodCallPacketData("getSmscAddress", String.class)).postMethodProcess(context);
    }

    public ResultTask<Void> injectSmsPdu(Byte[] pdu, String format, @Nullable PendingIntent receivedIntent) {
        return new ResultCreator<Void>(buildMethodCallPacketData("injectSmsPdu", Void.class,
                new Class[]{Byte.class, String.class, PendingIntent.class},
                pdu, format, receivedIntent)).postMethodProcess(context);
    }

    public ResultTask<Void> sendDataMessage(String destinationAddress, String scAddress, Short destinationPort, Byte[] data, PendingIntent sentIntent, PendingIntent deliveryIntent) {
        return new ResultCreator<Void>(buildMethodCallPacketData("sendDataMessage", Void.class,
                new Class<?>[]{String.class, String.class, String.class, Byte[].class, PendingIntent.class, PendingIntent.class},
                destinationAddress, scAddress, destinationPort, data, sentIntent, deliveryIntent)).postMethodProcess(context);
    }

    public ResultTask<Void> sendMultimediaMessage(Uri contentUri, String locationUrl, Bundle configOverrides, PendingIntent sentIntent) {
        return new ResultCreator<Void>(buildMethodCallPacketData("sendMultimediaMessage", Void.class,
                new Class<?>[]{Uri.class, String.class, Bundle.class, PendingIntent.class},
                contentUri, locationUrl, configOverrides, sentIntent)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public ResultTask<Void> sendMultimediaMessage(Uri contentUri, String locationUrl, Bundle configOverrides, PendingIntent sentIntent, Long messageId) {
        return new ResultCreator<Void>(buildMethodCallPacketData("sendMultimediaMessage", Void.class,
                new Class<?>[]{Uri.class, String.class, Bundle.class, PendingIntent.class, Long.class},
                contentUri, locationUrl, configOverrides, sentIntent, messageId)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<Void> sendMultipartTextMessage(String destinationAddress, String scAddress, ArrayList<String> parts, ArrayList<PendingIntent> sentIntents, ArrayList<PendingIntent> deliveryIntents, Long messageId) {
        return new ResultCreator<Void>(buildMethodCallPacketData("sendMultipartTextMessage", Void.class,
                new Class<?>[]{String.class, String.class, ArrayList.class, ArrayList.class, ArrayList.class, Long.class},
                destinationAddress, scAddress, parts, sentIntents, deliveryIntents, messageId)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<Void> sendMultipartTextMessage(String destinationAddress, String scAddress, ArrayList<String> parts, ArrayList<PendingIntent> sentIntents, ArrayList<PendingIntent> deliveryIntents) {
        return new ResultCreator<Void>(buildMethodCallPacketData("sendMultipartTextMessage", Void.class,
                new Class<?>[]{String.class, String.class, ArrayList.class, ArrayList.class, ArrayList.class},
                destinationAddress, scAddress, parts, sentIntents, deliveryIntents)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<Void> sendMultipartTextMessage(String destinationAddress, String scAddress, ArrayList<String> parts, ArrayList<PendingIntent> sentIntents, ArrayList<PendingIntent> deliveryIntents, String packageName, String attributionTag) {
        return new ResultCreator<Void>(buildMethodCallPacketData("sendMultipartTextMessage", Void.class,
                new Class<?>[]{String.class, String.class, ArrayList.class, ArrayList.class, ArrayList.class, String.class, String.class},
                destinationAddress, scAddress, parts, sentIntents, deliveryIntents, packageName, attributionTag)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<Void> sendTextMessage(String destinationAddress, String scAddress, String text, PendingIntent sentIntent, PendingIntent deliveryIntent, Long messageId) {
        return new ResultCreator<Void>(buildMethodCallPacketData("sendTextMessage", Void.class,
                new Class<?>[]{String.class, String.class, String.class, PendingIntent.class, PendingIntent.class, Long.class},
                destinationAddress, scAddress, text, sentIntent, deliveryIntent, messageId)).postMethodProcess(context);
    }

    public ResultTask<Void> sendTextMessage(String destinationAddress, String scAddress, String text, PendingIntent sentIntent, PendingIntent deliveryIntent) {
        return new ResultCreator<Void>(buildMethodCallPacketData("sendTextMessage", Void.class,
                new Class<?>[]{String.class, String.class, String.class, PendingIntent.class, PendingIntent.class},
                destinationAddress, scAddress, text, sentIntent, deliveryIntent)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<Boolean> setSmscAddress(@NonNull String smsc) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("setSmscAddress", Boolean.class, smsc)).postMethodProcess(context);
    }

    private PacketData buildMethodCallPacketData(String methodName, Class<?> parameterCls, Object... args) {
        return buildMethodCallPacketData(methodName, parameterCls, null, args);
    }

    private PacketData buildMethodCallPacketData(String methodName, Class<?> parameterCls, Class<?>[] classHint, Object... args) {
        Instance instance = Instance.getInstance();
        PacketData packet = new PacketData();
        String ticket = String.format("%s@%s", methodName, System.currentTimeMillis());

        ArgsInfo argsInfo = new ArgsInfo();
        argsInfo.put(smsSubscriptionId);
        argsInfo.put(parameterCls, methodName);

        int argCount = 0;
        for (Object arg : args) {
            if (arg == null) {
                argsInfo.put(classHint[argCount], null);
            } else if (arg instanceof ArrayList) {
                ArrayList<?> argsList = ((ArrayList<?>) arg);
                if (!argsList.isEmpty() && argsList.get(0) instanceof Parcelable) {
                    argsInfo.put(arg.getClass(), ProcessConst.KEY_PARCEL_REPLACED);
                    packet.parcelableList.add((Parcelable) arg);
                } else {
                    argsInfo.put(arg);
                    if (argsList.isEmpty()) {
                        Instance.printLog("Invalid Arguments: ArrayList is empty, therefore cannot determine list's generic type is Parcelable or not");
                    }
                }
            } else if (arg instanceof Parcelable) {
                argsInfo.put(arg.getClass(), ProcessConst.KEY_PARCEL_REPLACED);
                packet.parcelableList.add((Parcelable) arg);
            } else {
                argsInfo.put(arg);
            }
            argCount += 1;
        }

        packet.argsInfo = argsInfo;
        packet.fromPackageName = instance.getServerPeer().getPackageName();
        packet.ticketId = ticket;
        packet.apiType = ProcessConst.ACTION_TYPE_SMS;
        packet.actionType = ProcessConst.ACTION_REQUEST_METHOD;

        return packet;
    }
}

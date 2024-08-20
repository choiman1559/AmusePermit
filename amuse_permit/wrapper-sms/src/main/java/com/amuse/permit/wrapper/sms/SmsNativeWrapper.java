package com.amuse.permit.wrapper.sms;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.model.Annotations;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.model.Wrappable;

import java.util.ArrayList;

@SuppressWarnings("unused")
@Annotations.ResponserSide
public class SmsNativeWrapper extends SmsTokenModel {

    private Context context;
    private SmsManager smsManager;

    @Override
    public ResultTask<Wrappable> createServerInstance(@NonNull Context context, @NonNull ArgsInfo argsInfo) {
        ResultTask<Wrappable> nativeWrapperResultTask = new ResultTask<>();
        nativeWrapperResultTask.mOnInvokeAttached = result -> {
            ResultTask.Result<Wrappable> nativeResult = new ResultTask.Result<>();
            SmsNativeWrapper nativeWrapper = new SmsNativeWrapper();
            nativeWrapper.context = context;

            Integer smsSubscribeId = (Integer) argsInfo.getData(0);
            if(smsSubscribeId != null) {
                nativeWrapper.smsManager = SmsManager.getSmsManagerForSubscriptionId(smsSubscribeId);
            } else {
                nativeWrapper.smsManager = context.getSystemService(SmsManager.class);
            }

            nativeResult.setResultData(nativeWrapper);
            nativeResult.setSuccess(nativeWrapper.smsManager != null);
            nativeWrapperResultTask.callCompleteListener(nativeResult);
        };
        return nativeWrapperResultTask;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String createAppSpecificSmsToken(PendingIntent intent) {
        return smsManager.createAppSpecificSmsToken(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public String createAppSpecificSmsTokenWithPackageInfo(String prefixes, PendingIntent intent) {
        return smsManager.createAppSpecificSmsTokenWithPackageInfo(prefixes, intent);
    }

    public ArrayList<String> divideMessage(String text) {
        return smsManager.divideMessage(text);
    }

    public Void downloadMultimediaMessage(String locationUrl, Uri contentUri, Bundle configOverrides, PendingIntent downloadedIntent) {
        smsManager.downloadMultimediaMessage(context, locationUrl, contentUri, configOverrides, downloadedIntent);
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public Void downloadMultimediaMessage(String locationUrl, Uri contentUri, Bundle configOverrides, PendingIntent downloadedIntent, Long messageId) {
        smsManager.downloadMultimediaMessage(context, locationUrl, contentUri, configOverrides, downloadedIntent, messageId);
        return null;
    }

    public Bundle getCarrierConfigValues() {
        return smsManager.getCarrierConfigValues();
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.S)
    public Integer getSmsCapacityOnIcc() {
        return smsManager.getSmsCapacityOnIcc();
    }

    public Integer getDefaultSmsSubscriptionId() {
        return SmsManager.getDefaultSmsSubscriptionId();
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.R)
    public String getSmscAddress() {
        return smsManager.getSmscAddress();
    }

    public Void injectSmsPdu(Byte[] pdu, String format, PendingIntent receivedIntent) {
        smsManager.injectSmsPdu(convertByteArray(pdu), format, receivedIntent);
        return null;
    }

    public Void sendDataMessage(String destinationAddress, String scAddress, Short destinationPort, Byte[] data, PendingIntent sentIntent, PendingIntent deliveryIntent) {
        smsManager.sendDataMessage(destinationAddress, scAddress, destinationPort, convertByteArray(data), sentIntent, deliveryIntent);
        return null;
    }

    public Void sendMultimediaMessage(Uri contentUri, String locationUrl, Bundle configOverrides, PendingIntent sentIntent) {
        smsManager.sendMultimediaMessage(context, contentUri, locationUrl, configOverrides, sentIntent);
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public Void sendMultimediaMessage(Uri contentUri, String locationUrl, Bundle configOverrides, PendingIntent sentIntent, Long messageId) {
        smsManager.sendMultimediaMessage(context, contentUri, locationUrl, configOverrides, sentIntent, messageId);
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public Void sendMultipartTextMessage(String destinationAddress, String scAddress, ArrayList<String> parts, ArrayList<PendingIntent> sentIntents, ArrayList<PendingIntent> deliveryIntents, Long messageId) {
        smsManager.sendMultipartTextMessage(destinationAddress, scAddress,parts, sentIntents, deliveryIntents, messageId);
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public Void sendMultipartTextMessage(String destinationAddress, String scAddress, ArrayList<String> parts, ArrayList<PendingIntent> sentIntents, ArrayList<PendingIntent> deliveryIntents) {
        smsManager.sendMultipartTextMessage(destinationAddress, scAddress, parts, sentIntents, deliveryIntents);
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public Void sendMultipartTextMessage(String destinationAddress, String scAddress, ArrayList<String> parts, ArrayList<PendingIntent> sentIntents, ArrayList<PendingIntent> deliveryIntents, String packageName, String attributionTag) {
        smsManager.sendMultipartTextMessage(destinationAddress, scAddress, parts, sentIntents, deliveryIntents, packageName, attributionTag);
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public Void sendTextMessage(String destinationAddress, String scAddress, String text, PendingIntent sentIntent, PendingIntent deliveryIntent, Long messageId) {
        smsManager.sendTextMessage(destinationAddress, scAddress, text, sentIntent, deliveryIntent, messageId);
        return null;
    }

    public Void sendTextMessage(String destinationAddress, String scAddress, String text, PendingIntent sentIntent, PendingIntent deliveryIntent) {
        smsManager.sendTextMessage(destinationAddress, scAddress, text, sentIntent, deliveryIntent);
        return null;
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.R)
    public Boolean setSmscAddress(String smsc) {
        return smsManager.setSmscAddress(smsc);
    }

    private byte[] convertByteArray(Byte[] array) {
        byte[] bytes = new byte[array.length];
        for(int i = 0; i < array.length; i++) {
            bytes[i] = array[i];
        }
        return bytes;
    }
}

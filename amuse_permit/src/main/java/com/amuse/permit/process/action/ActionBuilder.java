package com.amuse.permit.process.action;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.amuse.permit.Instance;
import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.process.ProcessConst;

import java.io.Serializable;

public class ActionBuilder {

    Context context;
    String packageName;
    String ticketId;

    Instance instance;
    Intent intent;

    public ActionBuilder(Context context, String packageName, String ticketId, String apiType, String actionType) {
        instance = Instance.getInstance();
        intent = new Intent();
        intent.setAction(ProcessConst.PACKAGE_BROADCAST_ACTION);

        this.context = context;
        this.packageName = packageName;
        this.ticketId = ticketId;

        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra(ProcessConst.KEY_PACKAGE_NAME, instance.getAppPackageName());
        intent.putExtra(ProcessConst.KEY_TICKET_ID, ticketId);
        intent.putExtra(ProcessConst.KEY_API_TYPE, apiType);
        intent.putExtra(ProcessConst.KEY_ACTION_TYPE, actionType);
        intent.setComponent(new ComponentName(packageName, ProcessConst.PACKAGE_BROADCAST));
    }

    public ActionBuilder(Context context, PacketData packetData) {
        instance = Instance.getInstance();
        intent = new Intent();
        intent.setAction(ProcessConst.PACKAGE_BROADCAST_ACTION);

        this.context = context;
        this.packageName = packetData.fromPackageName;
        this.ticketId = packetData.ticketId;

        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra(ProcessConst.KEY_PACKAGE_NAME, instance.getAppPackageName());
        intent.putExtra(ProcessConst.KEY_TICKET_ID, ticketId);
        intent.putExtra(ProcessConst.KEY_API_TYPE, packetData.apiType);
        intent.putExtra(ProcessConst.KEY_ACTION_TYPE, packetData.actionType);
        intent.setComponent(new ComponentName(packageName, ProcessConst.PACKAGE_BROADCAST));
    }

    public ActionBuilder setBundle(Bundle bundle) {
        intent.putExtras(bundle);
        return this;
    }

    public ActionBuilder setArgs(ArgsInfo argsInfo) {
        intent.putExtra(ProcessConst.KEY_ARGS, (Serializable) argsInfo);
        return this;
    }

    public ActionBuilder setSerializable(Serializable serializable) {
        intent.putExtra(ProcessConst.KEY_EXTRA_DATA, serializable);
        return this;
    }

    public ActionBuilder setParcelable(Parcelable parcelable) {
        intent.putExtra(ProcessConst.KEY_EXTRA_DATA, parcelable);
        return this;
    }

    public void send() {
        context.sendBroadcast(intent);
        Instance.printLog( "Sent/ " + packageName + " " + intent.getStringExtra(ProcessConst.KEY_ACTION_TYPE));
    }
}

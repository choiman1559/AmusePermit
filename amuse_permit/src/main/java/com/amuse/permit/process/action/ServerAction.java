package com.amuse.permit.process.action;

import android.content.Context;
import android.os.Bundle;

import com.amuse.permit.Instance;
import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.model.Wrappable;
import com.amuse.permit.process.ProcessConst;

public class ServerAction extends ActionBuilder {

    public ServerAction(Context context, String packageName, String ticketId) {
        super(context, packageName, ticketId);
    }

    public ServerAction(Context context, PacketData packetData) {
        super(context, packetData);
    }

    public ServerAction setException(Exception exception) {
        Bundle bundle = new Bundle();
        bundle.putString(ProcessConst.KEY_TARGET, ProcessConst.ACTION_TYPE_HANDSHAKE);
        bundle.putString(ProcessConst.KEY_ACTION_TYPE, ProcessConst.ACTION_RESPONSE_EXCEPTION);

        setBundle(bundle);
        setSerializable(exception);
        return this;
    }

    public ServerAction setHandShake() {
        Instance instance = Instance.getInstance();
        Bundle extras = new Bundle();

        ArgsInfo argsInfo = new ArgsInfo();
        argsInfo.put(Integer.class, instance.getServiceFlag());
        argsInfo.put(Boolean.class, instance.packageNameFilter.accept(packageName));
        argsInfo.put(String[].class, instance.getServerFeaturedApis());

        extras.putString(ProcessConst.KEY_TARGET, ProcessConst.ACTION_TYPE_HANDSHAKE);
        extras.putString(ProcessConst.KEY_ACTION_TYPE, ProcessConst.ACTION_RESPONSE_HANDSHAKE);

        setArgs(argsInfo);
        setBundle(extras);
        return this;
    }

    public ServerAction pushClass(String apiType, Wrappable wrappable) {
        Bundle extras = new Bundle();
        extras.putString(ProcessConst.KEY_TARGET, apiType);
        extras.putString(ProcessConst.KEY_ACTION_TYPE, ProcessConst.ACTION_RESPONSE_EXCEPTION);

        setBundle(extras);
        setSerializable(wrappable);
        return this;
    }

    public ServerAction pushStream(String apiType, ArgsInfo argsInfo) {
        Bundle extras = new Bundle();
        extras.putString(ProcessConst.KEY_TARGET, apiType);
        extras.putString(ProcessConst.KEY_ACTION_TYPE, ProcessConst.ACTION_RESPONSE_EXCEPTION);
        extras.putSerializable(ProcessConst.KEY_ARGS, argsInfo);

        setBundle(extras);
        setArgs(argsInfo);
        return this;
    }
}

package com.amuse.permit.process.action;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;

import com.amuse.permit.Instance;
import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.model.Wrappable;
import com.amuse.permit.process.ProcessConst;

import java.util.ArrayList;

public class ServerAction extends ActionBuilder {

    public ServerAction(Context context, PacketData packetData) {
        super(context, packetData);
    }

    public ServerAction setException(Exception exception) {
        Bundle bundle = new Bundle();
        bundle.putString(ProcessConst.KEY_API_TYPE, ProcessConst.ACTION_TYPE_HANDSHAKE);
        bundle.putString(ProcessConst.KEY_ACTION_TYPE, ProcessConst.ACTION_RESPONSE_EXCEPTION);

        setBundle(bundle);
        setSerializable(exception);

        exception.printStackTrace();
        return this;
    }

    public ServerAction setHandShake() {
        Instance instance = Instance.getInstance();
        Bundle extras = new Bundle();

        ArgsInfo argsInfo = new ArgsInfo();
        argsInfo.put(Integer.class, instance.getServiceFlag());
        argsInfo.put(Boolean.class, instance.packageNameFilter.accept(packageName));
        argsInfo.put(String[].class, instance.getServerFeaturedApis());

        extras.putString(ProcessConst.KEY_API_TYPE, ProcessConst.ACTION_TYPE_HANDSHAKE);
        extras.putString(ProcessConst.KEY_ACTION_TYPE, ProcessConst.ACTION_RESPONSE_HANDSHAKE);

        setBundle(extras);
        setArgs(argsInfo);
        return this;
    }

    public ServerAction pushClass(String apiType, Wrappable wrappable) {
        Bundle extras = new Bundle();
        extras.putString(ProcessConst.KEY_API_TYPE, apiType);
        extras.putString(ProcessConst.KEY_ACTION_TYPE, ProcessConst.ACTION_RESPONSE_CLASS);

        setBundle(extras);
        setSerializable(wrappable);
        return this;
    }

    public ServerAction pushMember(String apiType, ArgsInfo argsInfo) {
        Bundle extras = new Bundle();
        extras.putString(ProcessConst.KEY_API_TYPE, apiType);
        extras.putString(ProcessConst.KEY_ACTION_TYPE, ProcessConst.ACTION_RESPONSE_METHOD);

        setBundle(extras);
        setArgs(argsInfo);
        return this;
    }

    public ServerAction pushMethod(String apiType, ArgsInfo argsInfo) {
        Bundle extras = new Bundle();
        extras.putString(ProcessConst.KEY_API_TYPE, apiType);
        extras.putString(ProcessConst.KEY_ACTION_TYPE, ProcessConst.ACTION_RESPONSE_METHOD);

        setBundle(extras);
        setArgs(argsInfo);
        return this;
    }

    public ServerAction pushMethod(String apiType, Parcelable parcelable) {
        Bundle extras = new Bundle();
        extras.putString(ProcessConst.KEY_API_TYPE, apiType);
        extras.putString(ProcessConst.KEY_ACTION_TYPE, ProcessConst.ACTION_RESPONSE_METHOD);

        ArgsInfo argsInfo = new ArgsInfo();
        argsInfo.put(parcelable.getClass(), ProcessConst.KEY_PARCEL_REPLACED);

        setBundle(extras);
        setArgs(argsInfo);
        setParcelable(parcelable);
        return this;
    }

    public ServerAction pushMethod(String apiType, ArrayList<Parcelable> parcelable) {
        Bundle extras = new Bundle();
        extras.putString(ProcessConst.KEY_API_TYPE, apiType);
        extras.putString(ProcessConst.KEY_ACTION_TYPE, ProcessConst.ACTION_RESPONSE_METHOD);

        ArgsInfo argsInfo = new ArgsInfo();
        argsInfo.put(parcelable.getClass(), ProcessConst.KEY_PARCEL_LIST_REPLACED);

        setBundle(extras);
        setArgs(argsInfo);
        setParcelable(parcelable);
        return this;
    }

    public ServerAction pushStream(String apiType, ArgsInfo argsInfo) {
        Bundle extras = new Bundle();
        extras.putString(ProcessConst.KEY_API_TYPE, apiType);
        extras.putString(ProcessConst.KEY_ACTION_TYPE, ProcessConst.ACTION_RESPONSE_STREAM);
        extras.putSerializable(ProcessConst.KEY_ARGS, argsInfo);

        setBundle(extras);
        setArgs(argsInfo);
        return this;
    }

    public ServerAction pushStream(String apiType, ArgsInfo argsInfo, ArrayList<Parcelable> parcelables) {
        Bundle extras = new Bundle();
        extras.putString(ProcessConst.KEY_API_TYPE, apiType);
        extras.putString(ProcessConst.KEY_ACTION_TYPE, ProcessConst.ACTION_RESPONSE_STREAM);
        extras.putSerializable(ProcessConst.KEY_ARGS, argsInfo);

        setBundle(extras);
        setArgs(argsInfo);
        setParcelable(parcelables);
        return this;
    }
}

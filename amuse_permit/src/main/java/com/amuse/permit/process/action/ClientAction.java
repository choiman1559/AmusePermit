package com.amuse.permit.process.action;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;

import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.process.ProcessConst;

import java.util.ArrayList;

public class ClientAction extends ActionBuilder {

    public ClientAction(Context context, String packageName, String ticketId, String apiType, String actionType) {
        super(context, packageName, ticketId, apiType, actionType);
    }

    public ClientAction(Context context, PacketData packetData) {
        super(context, packetData);
    }

    public ClientAction pushHandShake() {
        Bundle extras = new Bundle();
        extras.putString(ProcessConst.KEY_API_TYPE, ProcessConst.ACTION_TYPE_HANDSHAKE);
        extras.putString(ProcessConst.KEY_ACTION_TYPE, ProcessConst.ACTION_REQUEST_HANDSHAKE);

        setBundle(extras);
        return this;
    }

    public ClientAction pushClass(String apiType, ArgsInfo argsInfo) {
        Bundle extras = new Bundle();
        extras.putString(ProcessConst.KEY_API_TYPE, apiType);
        extras.putString(ProcessConst.KEY_ACTION_TYPE, ProcessConst.ACTION_REQUEST_CLASS);

        setBundle(extras);
        setArgs(argsInfo);
        return this;
    }

    public ClientAction pushMethod(String apiType, ArgsInfo argsInfo) {
        Bundle extras = new Bundle();
        extras.putString(ProcessConst.KEY_API_TYPE, apiType);
        extras.putString(ProcessConst.KEY_ACTION_TYPE, ProcessConst.ACTION_REQUEST_METHOD);

        setBundle(extras);
        setArgs(argsInfo);
        return this;
    }

    public ClientAction pushMethod(String apiType, ArgsInfo argsInfo, ArrayList<Parcelable> parcelableList) {
        Bundle extras = new Bundle();
        extras.putString(ProcessConst.KEY_API_TYPE, apiType);
        extras.putString(ProcessConst.KEY_ACTION_TYPE, ProcessConst.ACTION_REQUEST_METHOD);

        setBundle(extras);
        setArgs(argsInfo);
        setParcelable(parcelableList);
        return this;
    }

    public ClientAction pushStream(String apiType, ArgsInfo argsInfo) {
        return pushStream(apiType, argsInfo, null);
    }

    public ClientAction pushStream(String apiType, ArgsInfo argsInfo, ArrayList<Parcelable> parcelableList) {
        Bundle extras = new Bundle();
        extras.putString(ProcessConst.KEY_API_TYPE, apiType);
        extras.putString(ProcessConst.KEY_ACTION_TYPE, ProcessConst.ACTION_REQUEST_STREAM);

        setBundle(extras);
        setArgs(argsInfo);

        if(parcelableList != null) {
            setParcelable(parcelableList);
        }
        return this;
    }
}

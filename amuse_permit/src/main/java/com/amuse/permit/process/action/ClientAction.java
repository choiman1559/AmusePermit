package com.amuse.permit.process.action;

import android.content.Context;
import android.os.Bundle;

import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.process.ProcessConst;

public class ClientAction extends ActionBuilder {

    public ClientAction(Context context, String packageName, String ticketId) {
        super(context, packageName, ticketId);
    }

    public ClientAction(Context context, PacketData packetData) {
        super(context, packetData);
    }

    public ClientAction pushHandShake() {
        Bundle extras = new Bundle();
        extras.putString(ProcessConst.KEY_TARGET, ProcessConst.ACTION_TYPE_HANDSHAKE);
        extras.putString(ProcessConst.KEY_ACTION_TYPE, ProcessConst.ACTION_REQUEST_HANDSHAKE);

        setBundle(extras);
        return this;
    }

    public ClientAction pushMethod(String apiType, ArgsInfo argsInfo) {
        Bundle extras = new Bundle();
        extras.putString(ProcessConst.KEY_TARGET, apiType);
        extras.putString(ProcessConst.KEY_ACTION_TYPE, ProcessConst.ACTION_RESPONSE_METHOD);

        setArgs(argsInfo);
        setBundle(extras);
        return this;
    }
}

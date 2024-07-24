package com.amuse.permit.model;

import android.content.Context;
import android.os.Bundle;

import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.process.ProcessConst;
import com.amuse.permit.process.action.ServerAction;

public abstract class ServiceProcess implements Processable {

    PacketData packetData;

    @Override
    public void onPacketReceived(Context context, Bundle bundle) throws Exception {
        packetData = new PacketData(bundle);
        if(packetData.actionType == null) {
            throw new IllegalStateException("Action type must not be Null");
        }
    }

    public PacketData getPacketData() {
        return packetData;
    }
}

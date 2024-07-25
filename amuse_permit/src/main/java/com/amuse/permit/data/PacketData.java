package com.amuse.permit.data;

import android.os.Bundle;

import com.amuse.permit.process.ProcessConst;

public class PacketData {
    public String actionType;
    public String fromPackageName;
    public String ticketId;
    public ArgsInfo argsInfo;

    public PacketData() {

    }

    public PacketData(Bundle bundle) {
        this.actionType = bundle.getString(ProcessConst.KEY_ACTION_TYPE);
        this.fromPackageName = bundle.getString(ProcessConst.KEY_PACKAGE_NAME);
        this.ticketId = bundle.getString(ProcessConst.KEY_TICKET_ID);
        this.argsInfo = (ArgsInfo) bundle.getSerializable(ProcessConst.KEY_ARGS);
    }
}

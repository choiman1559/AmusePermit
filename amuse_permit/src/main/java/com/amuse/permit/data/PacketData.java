package com.amuse.permit.data;

import android.os.Bundle;
import android.os.Parcelable;

import com.amuse.permit.process.ProcessConst;

import java.util.ArrayList;

public class PacketData {
    public String apiType;
    public String actionType;
    public String fromPackageName;
    public String ticketId;
    public ArgsInfo argsInfo;
    public ArrayList<Parcelable> parcelableList;

    public PacketData() {
        parcelableList = new ArrayList<>();
    }

    public PacketData(Bundle bundle) {
        this.apiType = bundle.getString(ProcessConst.KEY_API_TYPE);
        this.actionType = bundle.getString(ProcessConst.KEY_ACTION_TYPE);
        this.fromPackageName = bundle.getString(ProcessConst.KEY_PACKAGE_NAME);
        this.ticketId = bundle.getString(ProcessConst.KEY_TICKET_ID);
        this.argsInfo = (ArgsInfo) bundle.getSerializable(ProcessConst.KEY_ARGS);
        this.parcelableList = bundle.getParcelableArrayList(ProcessConst.KEY_EXTRA_PARCEL_LIST_DATA);
    }
}

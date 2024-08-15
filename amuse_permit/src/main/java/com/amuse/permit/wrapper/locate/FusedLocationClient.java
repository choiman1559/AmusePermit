package com.amuse.permit.wrapper.locate;

import android.content.Context;
import android.location.Location;

import com.amuse.permit.Instance;
import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.model.Annotations;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.process.ProcessConst;
import com.amuse.permit.process.action.ResultCreator;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

@SuppressWarnings("unused")
public class FusedLocationClient extends LocateModel {

    @JsonIgnore
    private Context context;

    @Annotations.StaticMethod
    @Annotations.Constructor
    public static FusedLocationClient getFusedLocationClient(Context context) {
        FusedLocationClient client = new FusedLocationClient();
        client.context = context;
        return client;
    }

    public ResultTask<Boolean> flushLocations() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("flushLocations", Boolean.class)).postMethodProcess(context);
    }

    public ResultTask<Location> getLastLocation() {
        return new ResultCreator<Location>(buildMethodCallPacketData("getLastLocation", Location.class)).postMethodProcess(context);
    }

    public ResultTask<Location> getCurrentLocation(@LocationPriority Integer priority) {
        return new ResultCreator<Location>(buildMethodCallPacketData("getCurrentLocation", Location.class, priority)).postMethodProcess(context);
    }

    public ResultTask<Boolean> getLocationAvailability() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("getLocationAvailability", Boolean.class)).postMethodProcess(context);
    }

    public void setMockMode(boolean mockMode) {
        this.isMock = mockMode;
    }

    public void setMockLocation(Location location) {
        this.mockLocation = location;
    }

    @JsonGetter("isMock")
    protected boolean getMockMode() {
        return isMock;
    }

    @JsonGetter("mockLocation")
    protected Location getMockLocation() {
        return mockLocation;
    }

    private PacketData buildMethodCallPacketData(String methodName, Class<?> parameterCls, Object... args) {
        Instance instance = Instance.getInstance();
        PacketData packet = new PacketData();
        ArgsInfo argsInfo = new ArgsInfo();
        String ticket = String.format("%s@%s", methodName, System.currentTimeMillis());

        LocateModel locateModel = new LocateModel();
        locateModel.mockLocation = mockLocation;
        locateModel.isMock = isMock;

        argsInfo.put(locateModel);
        argsInfo.put(parameterCls, methodName);

        for(Object arg : args) {
            argsInfo.put(arg);
        }

        packet.argsInfo = argsInfo;
        packet.fromPackageName = instance.getServerPeer().getPackageName();
        packet.ticketId = ticket;
        packet.apiType = ProcessConst.ACTION_TYPE_LOCATION;
        packet.actionType = ProcessConst.ACTION_REQUEST_METHOD;

        return packet;
    }
}

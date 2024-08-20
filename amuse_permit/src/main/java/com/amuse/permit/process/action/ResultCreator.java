package com.amuse.permit.process.action;

import android.content.Context;

import com.amuse.permit.data.PacketData;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.process.ProcessRoute;

public class ResultCreator<T> {

    PacketData packetData;
    boolean isStream;

    public ResultCreator(PacketData packetData) {
        this.packetData = packetData;
    }

    public ResultCreator(PacketData packetData, boolean isStream) {
        this.packetData = packetData;
        this.isStream = isStream;
    }

    public ResultTask<T> postMethodProcess(Context context) {
        ResultTask<T> resultTask = new ResultTask<>();
        resultTask.mOnInvokeAttached = result -> {
            ProcessRoute.registerInnerResultTask(packetData.ticketId, resultTask);
            ClientAction clientAction = new ClientAction(context, packetData);

            if(this.isStream) {
                if(packetData.parcelableList != null) {
                    clientAction.pushStream(packetData.apiType, packetData.argsInfo, packetData.parcelableList);
                } else {
                    clientAction.pushStream(packetData.apiType, packetData.argsInfo);
                }
            } else if (this.packetData.parcelableList != null) {
                clientAction.pushMethod(packetData.apiType, packetData.argsInfo, packetData.parcelableList);
            } else {
                clientAction.pushMethod(packetData.apiType, packetData.argsInfo);
            }
            clientAction.send();
        };
        return resultTask;
    }
}

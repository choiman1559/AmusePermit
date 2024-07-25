package com.amuse.permit.process.action;

import android.content.Context;

import com.amuse.permit.data.PacketData;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.process.ProcessRoute;

public class ResultCreator<T> {

    PacketData packetData;

    public ResultCreator(PacketData packetData) {
        this.packetData = packetData;
    }

    public ResultTask<T> postMethodProcess(Context context) {
        ResultTask<T> resultTask = new ResultTask<>();
        resultTask.mOnInvokeAttached = result -> {
            ProcessRoute.registerInnerResultTask(packetData.ticketId, resultTask);
            ProcessRoute.resultTaskHashMap.put("", resultTask);

            new ClientAction(context, packetData)
                    .pushMethod(packetData.actionType, packetData.argsInfo)
                    .send();
        };
        return resultTask;
    }
}

package com.amuse.permit.wrapper.cursor;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.model.Annotations;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.process.ProcessConst;
import com.amuse.permit.process.ProcessRoute;
import com.amuse.permit.process.ProcessStream;
import com.amuse.permit.process.ServiceProcess;
import com.amuse.permit.process.action.ServerAction;

public class CursorProcess extends ServiceProcess {

    @Override
    public void onStreamRequested(Context context) {
        PacketData packetData = getPacketData();
        Uri uri = (Uri) packetData.parcelableList.get(0);
        ArgsInfo argsInfo = new ArgsInfo();

        if(uri != null) {
            ProcessStream.queryUriMap.put(packetData.ticketId, new Object[]{context, uri});
            argsInfo.put(true);
        } else {
            argsInfo.put(false);
        }

        new ServerAction(context, packetData)
                .pushStream(getType(), argsInfo)
                .send();
    }

    @Override
    public void onStreamResponded(Context context, Bundle bundle) {
        PacketData packetData = getPacketData();
        ResultTask.Result<Boolean> cursorUriRegisterResult = new ResultTask.Result<>();

        cursorUriRegisterResult.setSuccess((Boolean) packetData.argsInfo.getData(0));
        cursorUriRegisterResult.setHasException(false);
        ProcessRoute.callInnerResultTask(packetData.ticketId, cursorUriRegisterResult);
    }

    @Override
    public @Annotations.ApiTypes String getType() {
        return ProcessConst.ACTION_TYPE_CURSOR;
    }

    @Override
    public void onPacketReceived(Context context, Bundle bundle) throws Exception {
        super.onPacketReceived(context, bundle);
    }

    @Override
    public Class<?> getNativeImplClass() {
        return this.getClass();
    }
}

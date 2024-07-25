package com.amuse.permit.wrapper.locate;

import android.content.Context;
import android.os.Bundle;

import com.amuse.permit.model.ServiceProcess;
import com.amuse.permit.process.ProcessConst;

public class LocateProcessor extends ServiceProcess {
    @Override
    public String getType() {
        return ProcessConst.ACTION_TYPE_LOCATION;
    }

    @Override
    public void onPacketReceived(Context context, Bundle bundle) throws Exception {
        super.onPacketReceived(context, bundle);
    }

    @Override
    public Class<?> getNativeImplClass() {
        return null;
    }
}

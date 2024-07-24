package com.amuse.permit.wrapper.telephony;

import android.content.Context;
import android.os.Bundle;

import com.amuse.permit.model.ServiceProcess;
import com.amuse.permit.process.ProcessConst;

public class TelephonyProcess extends ServiceProcess {

    @Override
    public String getType() {
        return ProcessConst.ACTION_TYPE_TELEPHONY;
    }

    @Override
    public void onPacketReceived(Context context, Bundle bundle) throws Exception {
        super.onPacketReceived(context, bundle);
    }
}

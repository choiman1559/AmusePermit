package com.amuse.permit.wrapper.telephony;

import android.content.Context;
import android.os.Bundle;

import com.amuse.permit.model.Annotations;
import com.amuse.permit.process.ServiceProcess;
import com.amuse.permit.process.ProcessConst;

public class TelephonyProcess extends ServiceProcess {



    @Override
    public @Annotations.ApiTypes String getType() {
        return ProcessConst.ACTION_TYPE_TELEPHONY;
    }

    @Override
    public void onPacketReceived(Context context, Bundle bundle) throws Exception {
        super.onPacketReceived(context, bundle);
    }

    @Override
    public Class<?> getNativeImplClass() {
        return getDefaultNativeClass("Telephony");
    }
}

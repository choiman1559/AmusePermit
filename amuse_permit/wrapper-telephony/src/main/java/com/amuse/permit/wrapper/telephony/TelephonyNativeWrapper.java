package com.amuse.permit.wrapper.telephony;

import android.content.Context;

import androidx.annotation.NonNull;

import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.model.Wrappable;

public class TelephonyNativeWrapper extends Wrappable {
    @Override
    public ResultTask<Wrappable> createServerInstance(@NonNull Context context, @NonNull ArgsInfo argsInfo) {
        return super.createServerInstance(context, argsInfo);
    }
}

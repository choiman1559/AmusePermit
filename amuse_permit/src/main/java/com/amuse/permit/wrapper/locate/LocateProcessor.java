package com.amuse.permit.wrapper.locate;

import android.content.Context;
import android.os.Bundle;

import com.amuse.permit.process.ServiceProcess;
import com.amuse.permit.process.ProcessConst;

public class LocateProcessor extends ServiceProcess {
    @Override
    public String getType() {
        return ProcessConst.ACTION_TYPE_LOCATION;
    }

    @Override
    public Class<?> getNativeImplClass() {
        try {
            return Class.forName(String.format("%s.wrapper.%s.LocateNativeWrapper", ProcessConst.PACKAGE_MODULE, getType()));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}

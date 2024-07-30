package com.amuse.client;

import android.content.Context;
import android.util.Log;

import com.amuse.permit.Instance;

import java.util.Arrays;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();

        try {
            Instance instance = Instance.initialize(context, Instance.OPERATE_MODE_CLIENT);
            instance.setPrintDebugLog(true);

            Log.d("ddd", Arrays.toString(Instance.getAvailablePeers(context)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

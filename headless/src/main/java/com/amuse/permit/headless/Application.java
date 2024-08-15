package com.amuse.permit.headless;

import android.content.Context;
import android.util.Log;

import com.amuse.permit.Instance;
import com.amuse.permit.model.NameFilters;

import java.util.Arrays;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        InitializeLibrary(getApplicationContext());
    }

    public synchronized static void InitializeLibrary(Context context) {
        try {
            if(Instance.getInstance(true) == null) {
                Instance instance = Instance.initialize(context, Instance.OPERATE_MODE_SERVER);
                instance.setClientScope((NameFilters.NameFilter<String>) object -> true);
                instance.setPrintDebugLog(true);
                Log.d("dddd", Arrays.toString(Instance.getAvailablePeers(context))); //TODO: Remove
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package com.amuse.permit.headless;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.amuse.permit.Instance;
import com.amuse.permit.model.NameFilters;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.wrapper.locate.LocateWork;

import java.util.Arrays;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        InitializeLibrary(getApplicationContext());
    }

    static class LocateWorks extends LocateWork.LocateWorker {
        @Override
        public ResultTask<Location> onLocationRequested(Integer priority) {
            Log.d("LocateWorker", "onLocationRequested invoked with priority: " + priority);
            return super.onLocationRequested(priority);
        }
    }

    public synchronized static void InitializeLibrary(Context context) {
        try {
            if(Instance.getInstance(true) == null) {
                Instance instance = Instance.initialize(context, Instance.OPERATE_MODE_SERVER);
                instance.setClientScope((NameFilters.NameFilter<String>) object -> true);
                instance.setPrintDebugLog(true);
                LocateWork.registerWorker(new LocateWorks());

                Log.d("dddd", Arrays.toString(Instance.getAvailablePeers(context))); //TODO: Remove
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

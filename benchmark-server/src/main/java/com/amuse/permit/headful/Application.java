package com.amuse.permit.headful;

import android.content.Context;

import com.amuse.permit.Instance;
import com.amuse.permit.model.NameFilters;

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
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

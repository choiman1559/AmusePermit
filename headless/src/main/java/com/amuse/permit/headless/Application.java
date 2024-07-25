package com.amuse.permit.headless;

import com.amuse.permit.Instance;
import com.amuse.permit.model.NameFilters;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        try {
            Instance instance = Instance.initialize(getApplicationContext(), Instance.OPERATE_MODE_SERVER);
            instance.setClientScope((NameFilters.NameFilter<String>) object -> true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package com.amuse.client;

import android.content.Context;

import com.amuse.permit.Instance;
import com.amuse.permit.data.AppPeer;
import com.amuse.permit.model.ResultTask;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();

        try {
            Instance instance = Instance.initialize(context, Instance.OPERATE_MODE_CLIENT);
            ResultTask<AppPeer> serverPeer = AppPeer.fetchInformation(context, Instance.getAvailablePeers(context)[0]);
            serverPeer.setOnTaskCompleteListener(result -> {
               if(result.isSuccess()) {
                   instance.setServerPeer((AppPeer) result.getResultData());
               }
            }).invokeTask();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

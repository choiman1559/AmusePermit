package com.amuse.client;

import android.content.Context;
import android.util.Log;

import com.amuse.permit.Instance;
import com.amuse.permit.data.AppPeer;
import com.amuse.permit.model.ResultTask;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();

        try {
            Instance instance = Instance.initialize(context, Instance.OPERATE_MODE_CLIENT);
            Log.d("ddd", Arrays.toString(Instance.getAvailablePeers(context)));
            ResultTask<AppPeer> serverPeer = AppPeer.fetchInformation(context,"com.amuse.permit.headless");
            serverPeer.setOnTaskCompleteListener(result -> {
                try {
                    Log.d("ddd", new ObjectMapper().writeValueAsString(result));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                if(result.isSuccess()) {
                   instance.setServerPeer((AppPeer) result.getResultData());
               }
            }).invokeTask();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasApiFeature(String type) {
        String[] featureArray = Instance.getInstance().getServerPeer().getFeaturedApis();
        for(String feature : featureArray) {
            if(feature.equals(type)) return true;
        }
        return false;
    }
}

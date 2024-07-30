package com.amuse.permit.data;

import android.content.Context;

import com.amuse.permit.Instance;
import com.amuse.permit.model.Annotations;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.process.ProcessConst;
import com.amuse.permit.process.action.ClientAction;
import com.amuse.permit.process.ProcessRoute;

public class AppPeer {
    private final String packageName;
    private final Integer mode;
    private final Boolean isAllowed;
    private final String[] featuredApis;

    public AppPeer(String packageName, Integer mode, Boolean isAllowed, String[] featuredApis) {
        this.packageName = packageName;
        this.mode = mode;
        this.isAllowed = isAllowed;
        this.featuredApis = featuredApis;
    }

    public static ResultTask<AppPeer> fetchInformation(Context context, String packageName) {
        ResultTask<AppPeer> resultTask = new ResultTask<>();
        resultTask.mOnInvokeAttached = (r) -> {
            String ticket = packageName + System.currentTimeMillis();
            ProcessRoute.registerInnerResultTask(ticket, resultTask);
            new ClientAction(context, packageName, ticket, ProcessConst.ACTION_TYPE_HANDSHAKE, ProcessConst.ACTION_REQUEST_HANDSHAKE)
                    .pushHandShake()
                    .send();
        };
        return resultTask;
    }

    public Boolean isAllowedByPeer() {
        return isAllowed;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isModeFlagSupported(int flags) {
        return (mode | flags) == mode;
    }

    public String[] getFeaturedApis() {
        return featuredApis;
    }

    public boolean hasApiFeature(@Annotations.ApiTypes String type) {
        String[] featureArray = Instance.getInstance().getServerPeer().getFeaturedApis();
        for(String feature : featureArray) {
            if(feature.equals(type)) return true;
        }
        return false;
    }
}

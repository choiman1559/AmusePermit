package com.amuse.permit.data;

import android.content.Context;

import com.amuse.permit.Instance;
import com.amuse.permit.model.Annotations;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.process.ProcessConst;
import com.amuse.permit.process.action.ClientAction;
import com.amuse.permit.process.ProcessRoute;

/**
 * Information classes for other AmusePermit applications
 */
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

    /**
     * Retrieves information from a specified AmusePermit application.
     *
     * @see Instance#setServerPeer(AppPeer)
     * @param context Application context instance
     * @param packageName application package name that trying to fetch information
     * @return information about the specified application
     */
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

    /**
     * Check whether the given application allows this current application
     *
     * @return true if the peer allows this application
     */
    public Boolean isAllowedByPeer() {
        return isAllowed;
    }

    /**
     * Return package name of the specified AmusePermit application.
     *
     * @return the application package name
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Checks whether current AmusePermit operation mode has specified flag
     *
     * @param flags flag to check
     * @return true if mode has specified flag
     */
    public boolean isModeFlagSupported(@Annotations.ServerModes int flags) {
        return (mode | flags) == mode;
    }

    /**
     * Returns array of available Api types
     *
     * @return  String Array of available Api types
     */
    public String[] getFeaturedApis() {
        return featuredApis;
    }

    /**
     * Check whether the specific api type is supported
     *
     * @param type The api type to check
     * @return true if the api type is supported
     */
    public boolean hasApiFeature(@Annotations.ApiTypes String type) {
        String[] featureArray = Instance.getInstance().getServerPeer().getFeaturedApis();
        for(String feature : featureArray) {
            if(feature.equals(type)) return true;
        }
        return false;
    }
}

package com.amuse.permit.wrapper.pkg;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.ModuleInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import com.amuse.permit.Instance;
import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.model.Annotations;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.process.ProcessConst;
import com.amuse.permit.process.action.ResultCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * Class for retrieving various kinds of information related to the application
 * packages that are currently installed on the device.
 * @see android.content.pm.PackageManager
 */
@Annotations.RequesterSide
@SuppressWarnings("unused")
public class PackageManager extends QueryPkgModel {

    @JsonIgnore
    private Context context;

    /**
     * Get default {@link PackageManager} instance,
     * equivalent constructor with {@link Context#getPackageManager()}
     *
     * @param context Application context instance
     * @return the default {@link PackageManager} instance
     */
    @Annotations.Constructor
    public static PackageManager getPackageManager(Context context) {
        PackageManager packageManager = new PackageManager();
        packageManager.context = context;
        return packageManager;
    }

    /**
     * Return a List of all modules that are installed.
     *
     * @param flags Additional option flags to modify the data returned.
     * @return A {@link List} of {@link ModuleInfo} objects, one for each installed
     *         module, containing information about the module. In the unlikely case
     *         there are no installed modules, an empty list is returned.
     * @see android.content.pm.PackageManager#getInstalledApplications(int)
     */
    public ResultTask<List<ApplicationInfo>> getInstalledApplications (int flags) {
        return new ResultCreator<List<ApplicationInfo>>(buildMethodCallPacketData("getInstalledApplications", List.class, flags)).postMethodProcess(context);
    }

    /**
     * Retrieve all activities that can be performed for the given intent.
     *
     * @param intent The desired intent as per resolveActivity().
     * @param flags Additional option flags to modify the data returned. The
     *            most important is {@link android.content.pm.PackageManager#MATCH_DEFAULT_ONLY}, to limit the
     *            resolution to only those activities that support the
     *            {@link android.content.Intent#CATEGORY_DEFAULT}. Or, set
     *            {@link android.content.pm.PackageManager#MATCH_ALL} to prevent any filtering of the results.
     * @return Returns a List of ResolveInfo objects containing one entry for
     *         each matching activity, ordered from best to worst. In other
     *         words, the first item is what would be returned by
     *         {@link android.content.pm.PackageManager#resolveActivity}. If there are no matching activities, an
     *         empty list is returned.
     * @see android.content.pm.PackageManager#queryIntentActivities(Intent, int)
     */
    public ResultTask<List<ResolveInfo>> queryIntentActivities (Intent intent, int flags) {
        return new ResultCreator<List<ResolveInfo>>(buildMethodCallPacketData("queryIntentActivities", List.class, intent, flags)).postMethodProcess(context);
    }

    /**
     * Retrieve all receivers that can handle a broadcast of the given intent.
     *
     * @param intent The desired intent as per resolveActivity().
     * @param flags Additional option flags to modify the data returned.
     * @return Returns a List of ResolveInfo objects containing one entry for
     *         each matching receiver, ordered from best to worst. If there are
     *         no matching receivers, an empty list or null is returned.
     * @see android.content.pm.PackageManager#queryBroadcastReceivers(Intent, int)
     */
    public ResultTask<List<ResolveInfo>> queryBroadcastReceivers (Intent intent, int flags) {
        return new ResultCreator<List<ResolveInfo>>(buildMethodCallPacketData("queryBroadcastReceivers", List.class, intent, flags)).postMethodProcess(context);
    }

    /**
     * Retrieve all services that can match the given intent.
     *
     * @param intent The desired intent as per resolveService().
     * @param flags Additional option flags to modify the data returned.
     * @return Returns a List of ResolveInfo objects containing one entry for
     *         each matching service, ordered from best to worst. In other
     *         words, the first item is what would be returned by
     *         {@link android.content.pm.PackageManager#resolveService}. If there are no matching services, an
     *         empty list or null is returned.
     * @see android.content.pm.PackageManager#queryIntentServices(Intent, int)
     */
    public ResultTask<List<ResolveInfo>> queryIntentServices (Intent intent, int flags) {
        return new ResultCreator<List<ResolveInfo>>(buildMethodCallPacketData("queryIntentServices", List.class, intent, flags)).postMethodProcess(context);
    }

    /**
     * Retrieve all providers that can match the given intent.
     *
     * @param intent An intent containing all of the desired specification
     *            (action, data, type, category, and/or component).
     * @param flags Additional option flags to modify the data returned.
     * @return Returns a List of ResolveInfo objects containing one entry for
     *         each matching provider, ordered from best to worst. If there are
     *         no matching services, an empty list or null is returned.
     * @see android.content.pm.PackageManager#queryIntentContentProviders(Intent, int)
     */
    public ResultTask<List<ResolveInfo>> queryIntentContentProviders (Intent intent, int flags) {
        return new ResultCreator<List<ResolveInfo>>(buildMethodCallPacketData("queryIntentContentProviders", List.class, intent, flags)).postMethodProcess(context);
    }

    /**
     * Retrieve information about available instrumentation code. May be used to
     * retrieve either all instrumentation code, or only the code targeting a
     * particular package.
     *
     * @param targetPackage If null, all instrumentation is returned; only the
     *            instrumentation targeting this package name is returned.
     * @param flags Additional option flags to modify the data returned.
     * @return A list of {@link InstrumentationInfo} objects containing one
     *         entry for each matching instrumentation. If there are no
     *         instrumentation available, returns an empty list.
     * @see android.content.pm.PackageManager#queryInstrumentation(String, int)
     */
    public ResultTask<List<InstrumentationInfo>> queryInstrumentation (String targetPackage, int flags) {
        return new ResultCreator<List<InstrumentationInfo>>(buildMethodCallPacketData("queryInstrumentation", List.class, targetPackage, flags)).postMethodProcess(context);
    }

    /**
     * Query for all of the permissions associated with a particular group.
     *
     * @param permissionGroup The fully qualified name (i.e. com.google.permission.LOGIN)
     *            of the permission group you are interested in. Use {@code null} to
     *            find all of the permissions not associated with a group.
     * @param flags Additional option flags to modify the data returned.
     * @return Returns a list of {@link PermissionInfo} containing information
     *         about all of the permissions in the given group.
     * @see android.content.pm.PackageManager#queryPermissionsByGroup(String, int)
     */
    @Deprecated(since = "This function returns blank Array since SDK 33. Instead, Use PackageManager.getPackageInfo and then get PackageInfo.requestedPermissions (returns String[])")
    public ResultTask<List<PermissionInfo>> queryPermissionsByGroup (String permissionGroup, int flags) {
        return new ResultCreator<List<PermissionInfo>>(buildMethodCallPacketData("queryPermissionsByGroup", List.class, permissionGroup, flags)).postMethodProcess(context);
    }

    /**
     * Retrieve overall information about an application package that is
     * installed on the system.
     *
     * @param packageName The full name (i.e. com.google.apps.contacts) of the
     *            desired package.
     * @param flags Additional option flags to modify the data returned.
     * @return A PackageInfo object containing information about the package. If
     *         flag {@code MATCH_UNINSTALLED_PACKAGES} is set and if the package
     *         is not found in the list of installed applications, the package
     *         information is retrieved from the list of uninstalled
     *         applications (which includes installed applications as well as
     *         applications with data directory i.e. applications which had been
     *         deleted with {@code DELETE_KEEP_DATA} flag set).
     * @see android.content.pm.PackageManager#getPackageInfo(String, int)
     */
    public ResultTask<PackageInfo> getPackageInfo (String packageName, int flags) {
        return new ResultCreator<PackageInfo>(buildMethodCallPacketData("getPackageInfo", PackageInfo.class, packageName, flags)).postMethodProcess(context);
    }

    /**
     * Returns {@code true} if the source package is able to query for details about the
     * target package. Applications that share details about other applications should
     * use this API to determine if those details should be withheld from callers that
     * do not otherwise have visibility of them.
     * <p>
     * Note: The caller must be able to query for details about the source and target
     * package. A {@link android.content.pm.PackageManager.NameNotFoundException} is thrown if it isn't.
     *
     * @param sourcePackage The source package that would receive details about the
     *                          target package.
     * @param targetPackages The target package whose details would be shared with the
     *                          source package.
     * @return {@code true} if the source package is able to query for details about the
     * target package.
     * @see android.content.pm.PackageManager#canPackageQuery(String, String[])
     */
    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public ResultTask<Boolean[]> canPackageQuery (String sourcePackage, String[] targetPackages) {
        return new ResultCreator<Boolean[]>(buildMethodCallPacketData("canPackageQuery", Boolean[].class, sourcePackage, targetPackages)).postMethodProcess(context);
    }

    private PacketData buildMethodCallPacketData(String methodName, Class<?> parameterCls, Object... args) {
        Instance instance = Instance.getInstance();
        PacketData packet = new PacketData();
        String ticket = String.format("%s@%s", methodName, System.currentTimeMillis());

        ArgsInfo argsInfo = new ArgsInfo();
        argsInfo.put(parameterCls, methodName);

        for(Object arg : args) {
            if(arg instanceof Parcelable) {
                argsInfo.put(arg.getClass(), ProcessConst.KEY_PARCEL_REPLACED);
                packet.parcelableList.add((Parcelable) arg);
            } else {
                argsInfo.put(arg);
            }
        }

        packet.argsInfo = argsInfo;
        packet.fromPackageName = instance.getServerPeer().getPackageName();
        packet.ticketId = ticket;
        packet.apiType = ProcessConst.ACTION_TYPE_PACKAGE;
        packet.actionType = ProcessConst.ACTION_REQUEST_METHOD;

        return packet;
    }
}

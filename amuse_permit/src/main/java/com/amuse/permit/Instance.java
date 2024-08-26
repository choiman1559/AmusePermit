package com.amuse.permit;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.amuse.permit.data.AppPeer;
import com.amuse.permit.model.Annotations;
import com.amuse.permit.model.NameFilters;
import com.amuse.permit.model.Processable;
import com.amuse.permit.process.ProcessConst;
import com.amuse.permit.wrapper.cursor.CursorProcess;
import com.amuse.permit.wrapper.file.FileProcessor;
import com.amuse.permit.wrapper.locate.LocateProcessor;
import com.amuse.permit.wrapper.pkg.PackageProcess;
import com.amuse.permit.wrapper.sms.SmsProcess;
import com.amuse.permit.wrapper.telephony.TelephonyProcess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Instance class of AmusePermit
 */
@SuppressWarnings("unused")
public class Instance {

    /**
     * Operates as server mode, Provide data to other apps using this app's permissions
     */
    public static final int OPERATE_MODE_SERVER = 1;

    /**
     * Operates as client mode, Receive and use data from server applications
     */
    public static final int OPERATE_MODE_CLIENT = 2;

    public volatile static Instance instance;

    @Annotations.ServerModes
    private int serviceMode = 0;
    private String appPackageName;
    private AppPeer serverPeer;

    private final String LOG_TAG = "AmusePermission";
    private boolean printLog;

    public NameFilters.NameFilter<String> packageNameFilter;
    public HashMap<String, Processable> processableMap;
    public NameFilters.NameFilter<String> apiNameFilter;
    private ArrayList<@Annotations.ApiTypes String> serverFeaturedApis;

    private Instance() throws Exception {
        initializeServices();
    }

    /**
     * get previously initialized AmusePermit instance
     * This method must be called after calling initialize() method at least once
     *
     * @param context Android application class context
     * @param mode AmusePermit operation mode, Can use both flags using or(|) operator
     * @return an AmusePermit instance
     * @throws NullPointerException throws when previously initialized AmusePermit instance is not available
     */
    public static Instance initialize(Context context, @Annotations.ServerModes int mode) throws Exception {
        Instance.instance = new Instance();
        instance.serviceMode = mode;
        instance.appPackageName = context.getPackageName();
        return instance;
    }

    private void initializeServices() throws Exception {
        processableMap = new HashMap<>();
        serverFeaturedApis = new ArrayList<>();

        addService(FileProcessor.class);
        addService(LocateProcessor.class);
        addService(PackageProcess.class);
        addService(TelephonyProcess.class);
        addService(CursorProcess.class);
        addService(SmsProcess.class);
    }

    /**
     * get previously initialized AmusePermit instance
     * This method must be called after calling initialize() method at least once
     *
     * @return an AmusePermit instance
     * @throws NullPointerException throws when previously initialized AmusePermit instance is not available
     */
    public static Instance getInstance() {
        return getInstance(false);
    }

    /**
     * get previously initialized AmusePermit instance
     * This method must be called after calling initialize() method at least once
     *
     * @return an AmusePermit instance
     * @param allowNull when true, allows that return null if instance is null, otherwise throws NullPointerException
     * @throws NullPointerException throws when previously initialized AmusePermit instance is not available
     */
    public static Instance getInstance(boolean allowNull) {
        if(instance == null && !allowNull) {
            throw new NullPointerException("Instance is not initialized");
        }

        return instance;
    }

    /**
     * Scopes the specified api types (without {@link ProcessConst#ACTION_REQUEST_HANDSHAKE} api).
     * Api type not allowed when filter object returns false,
     * otherwise allow the specified client packages.
     *
     * @see NameFilters.NameFilter
     * @param apiNameFilter filter object that will be used to scope api types
     * @throws Exception throws when filter throws exceptions
     */
    public void setFeaturedApiTypeScope(NameFilters.NameFilter<@Annotations.ApiTypes String> apiNameFilter) throws Exception {
        this.apiNameFilter = apiNameFilter;
        initializeServices();
    }

    /**
     * Scopes the specified packages name.
     * Client not allowed when filter object returns false,
     * otherwise allow the specified client packages.
     *
     * @see NameFilters.NameFilter
     * @param packageNameFilter filter object that will be used to scope client package names
     */
    public void setClientScope(NameFilters.NameFilter<String> packageNameFilter) {
        this.packageNameFilter = packageNameFilter;
    }

    private void addService(@NonNull Class<?> cls) throws Exception {
        Processable processable = (Processable) cls.getDeclaredConstructor().newInstance();
        this.processableMap.put(processable.getType(), processable);

        try {
            if (processable.getNativeImplClass() != null) {
                processable.getNativeImplClass().newInstance();
            } else {
                return;
            }
        } catch (IllegalAccessException | InstantiationException e) {
            //Native implementation Module is not dependent on this project; Rejects this api for server usage
            return;
        }

        if(apiNameFilter == null || apiNameFilter.accept(processable.getType())) {
            this.serverFeaturedApis.add(processable.getType());
        }
    }

    /**
     * Set Specifies the server application from which data will be received.
     *
     * @see AppPeer#fetchInformation(Context, String)
     * @param serverPeer AppPeer instance to designate as server
     */
    public void setServerPeer(AppPeer serverPeer) {
        if(hasModFlag(OPERATE_MODE_SERVER)) {
            this.serverPeer = serverPeer;
        }
    }

    /**
     * Select whether to enable debugging output in AmusePermit.
     *
     * @see #printLog(String)
     * @param printLog Output debugging log when true
     */
    public void setPrintDebugLog(boolean printLog) {
        this.printLog = printLog;
    }

    /**
     * Returns current AmusePermit operation mode, to check whether server mode or client mode
     *
     * @see #initialize(Context, int)
     * @return either {@link #OPERATE_MODE_CLIENT} or {@link #OPERATE_MODE_SERVER}
     */
    public @Annotations.ServerModes int getServiceFlag() {
        return serviceMode;
    }

    /**
     * Checks whether current AmusePermit operation mode has specified flag
     *
     * @see #getServiceFlag()
     * @param flag flag to check
     * @return true if mode has specified flag
     */
    public boolean hasModFlag(@Annotations.ServerModes int flag) {
        return (serviceMode | flag) == serviceMode;
    }

    /**
     * Returns this application's package name, not AmusePermit's package name,
     * equivalent to {@link Context#getPackageName()}.
     *
     * @return Current process package name
     */
    public String getAppPackageName() {
        return appPackageName;
    }

    /**
     * Returns array of available AmusePermit-compatibility applications that installed on the current device,
     * using {@link ProcessConst#PACKAGE_BROADCAST_ACTION} action name.
     *
     * @param context Application context instance
     * @return String Array of available AmusePermit-compatibility applications package names
     */
    public static String[] getAvailablePeers(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(ProcessConst.PACKAGE_BROADCAST_ACTION);
        List<ResolveInfo> listApps;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listApps = packageManager.queryBroadcastReceivers(intent, PackageManager.ResolveInfoFlags.of(0));
        } else listApps = packageManager.queryBroadcastReceivers(intent, 0);

        String[] listArr = new String[listApps.size() - 1];
        for (int i = 0; i < listArr.length; i++) {
            String packageName = listApps.get(i).activityInfo.packageName;
            Instance.printLog(packageName);

            if(packageName.equals(getInstance().appPackageName)) {
                continue;
            }
            listArr[i] = packageName;
        }
        return listArr;
    }

    /**
     * Returns array of available Api types
     * using {@link com.amuse.permit.model.NameFilters.NameFilter} filter
     * that designated previously.
     *
     * @see #setFeaturedApiTypeScope(NameFilters.NameFilter)
     * @return String Array of available Api types
     */
    public @Annotations.ApiTypes String[] getServerFeaturedApis() {
        ArrayList<String> dataArray = new ArrayList<>();
        for(String apiName : serverFeaturedApis) {
            if(apiNameFilter == null || apiNameFilter.accept(apiName)) {
                dataArray.add(apiName);
            }
        }

        @Annotations.ApiTypes
        String[] data = new String[dataArray.size()];
        dataArray.toArray(data);
        return data;
    }

    /**
     * Get server application information that previously set.
     *
     * @see #setServerPeer(AppPeer)
     * @return the server peer information object
     */
    public AppPeer getServerPeer() {
        return serverPeer;
    }

    /**
     * Check if AmusePermit is enabled debug logging
     *
     * @see #setPrintDebugLog(boolean)
     * @return return true if debug logging is enabled
     */
    public boolean isPrintLog() {
        return printLog;
    }

    /**
     * Print logs to Standard Android logging output ({@link Log#d})
     *
     * @see #setPrintDebugLog(boolean)
     * @param msg message to print Logcat
     */
    public static void printLog(String msg) {
        Instance instance = getInstance();
        if(instance.printLog) {
            Log.d(instance.LOG_TAG, msg);
        }
    }
}

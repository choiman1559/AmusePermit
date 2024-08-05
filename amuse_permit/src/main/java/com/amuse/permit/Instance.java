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
import com.amuse.permit.wrapper.file.FileProcessor;
import com.amuse.permit.wrapper.locate.LocateProcessor;
import com.amuse.permit.wrapper.pkg.PackageProcess;
import com.amuse.permit.wrapper.telephony.TelephonyProcess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class Instance {
    /***
     * Have to Implement these API wrappers
     * • SMS & Phone Call State
     * • Geo Location
     * • File I/O
     * • Package Query & Management
     */

    public static final int OPERATE_MODE_SERVER = 1;
    public static final int OPERATE_MODE_CLIENT = 2;

    public volatile static Instance instance;

    private int serviceMode = 0;
    private String appPackageName;
    private AppPeer serverPeer;

    private final String LOG_TAG = "AmusePermission";
    private boolean printLog;

    public NameFilters.NameFilter<String> packageNameFilter;
    public HashMap<String, Processable> processableMap;
    public NameFilters.NameFilter<String> apiNameFilter;
    private ArrayList<String> serverFeaturedApis;

    private Instance() throws Exception {
        initializeServices();
    }

    public static Instance initialize(Context context, int mode) throws Exception {
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
    }

    public static Instance getInstance() {
        return getInstance(false);
    }

    public static Instance getInstance(boolean allowNull) {
        if(instance == null && !allowNull) {
            throw new NullPointerException("Instance is not initialized");
        }

        return instance;
    }

    public void setFeaturedApiTypeScope(NameFilters.NameFilter<@Annotations.ApiTypes String> apiNameFilter) throws Exception {
        this.apiNameFilter = apiNameFilter;
        initializeServices();
    }

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

    public void setServerPeer(AppPeer serverPeer) {
        if(hasModFlag(OPERATE_MODE_CLIENT)) {
            this.serverPeer = serverPeer;
        }
    }

    public void setPrintDebugLog(boolean printLog) {
        this.printLog = printLog;
    }

    public int getServiceFlag() {
        return serviceMode;
    }

    public boolean hasModFlag(int flag) {
        return (serviceMode | flag) == serviceMode;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

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

    public String[] getServerFeaturedApis() {
        ArrayList<String> dataArray = new ArrayList<>();
        for(String apiName : serverFeaturedApis) {
            if(apiNameFilter == null || apiNameFilter.accept(apiName)) {
                dataArray.add(apiName);
            }
        }

        String[] data = new String[dataArray.size()];
        dataArray.toArray(data);
        return data;
    }

    public AppPeer getServerPeer() {
        return serverPeer;
    }

    public boolean isPrintLog() {
        return printLog;
    }

    public static void printLog(String msg) {
        Instance instance = getInstance();
        if(instance.printLog) {
            Log.d(instance.LOG_TAG, msg);
        }
    }
}

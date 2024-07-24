package com.amuse.permit;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;

import androidx.annotation.NonNull;

import com.amuse.permit.data.AppPeer;
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

    public NameFilters.NameFilter<String> packageNameFilter;
    public final HashMap<String, Processable> processableMap;
    private final ArrayList<String> serverFeaturedApis;

    private Instance() throws Exception {
        processableMap = new HashMap<>();
        serverFeaturedApis = new ArrayList<>();

        addService(FileProcessor.class);
        addService(LocateProcessor.class);
        addService(PackageProcess.class);
        addService(TelephonyProcess.class);
    }

    public static Instance initialize(Context context, int mode) throws Exception {
        Instance.instance = new Instance();
        instance.serviceMode = mode;
        instance.appPackageName = context.getPackageName();
        return instance;
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

    public void setClientScope(NameFilters.NameFilter<String> packageNameFilter) {
        this.packageNameFilter = packageNameFilter;
    }

    private void addService(@NonNull Class<?> cls) throws Exception {
        Processable processable = (Processable) cls.getDeclaredConstructor().newInstance();
        this.processableMap.put(processable.getType(), processable);
        this.serverFeaturedApis.add(processable.getType());
    }

    public void setServerPeer(AppPeer serverPeer) {
        this.serverPeer = serverPeer;
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

        String[] listArr = new String[listApps.size()];
        for (int i = 0; i < listArr.length; i++) {
            String packageName = listApps.get(i).activityInfo.packageName;
            listArr[i] = packageName;
        }
        return listArr;
    }

    public String[] getServerFeaturedApis() {
        String[] data = new String[serverFeaturedApis.size()];
        serverFeaturedApis.toArray(data);
        return data;
    }
}

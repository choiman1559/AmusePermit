package com.amuse.permit.wrapper.pkg;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.InstrumentationInfo;
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

@Annotations.RequesterSide
@SuppressWarnings("unused")
public class PackageManager {

    @JsonIgnore
    private Context context;

    @Annotations.Constructor
    public static PackageManager getPackageManager(Context context) {
        PackageManager packageManager = new PackageManager();
        packageManager.context = context;
        return packageManager;
    }

    public ResultTask<List<ApplicationInfo>> getInstalledApplications (int flags) {
        return new ResultCreator<List<ApplicationInfo>>(buildMethodCallPacketData("getInstalledApplications", List.class, flags)).postMethodProcess(context);
    }

    public ResultTask<List<ResolveInfo>> queryIntentActivities (Intent intent, int flags) {
        return new ResultCreator<List<ResolveInfo>>(buildMethodCallPacketData("queryIntentActivities", List.class, intent, flags)).postMethodProcess(context);
    }

    public ResultTask<List<ResolveInfo>> queryBroadcastReceivers (Intent intent, int flags) {
        return new ResultCreator<List<ResolveInfo>>(buildMethodCallPacketData("queryBroadcastReceivers", List.class, intent, flags)).postMethodProcess(context);
    }

    public ResultTask<List<ResolveInfo>> queryIntentServices (Intent intent, int flags) {
        return new ResultCreator<List<ResolveInfo>>(buildMethodCallPacketData("queryIntentServices", List.class, intent, flags)).postMethodProcess(context);
    }

    public ResultTask<List<ResolveInfo>> queryIntentContentProviders (Intent intent, int flags) {
        return new ResultCreator<List<ResolveInfo>>(buildMethodCallPacketData("queryIntentContentProviders", List.class, intent, flags)).postMethodProcess(context);
    }

    public ResultTask<List<InstrumentationInfo>> queryInstrumentation (String targetPackage, int flags) {
        return new ResultCreator<List<InstrumentationInfo>>(buildMethodCallPacketData("queryInstrumentation", List.class, targetPackage, flags)).postMethodProcess(context);
    }

    @Deprecated(since = "This function returns blank Array since SDK 33. Instead, Use PackageManager.getPackageInfo and then get PackageInfo.requestedPermissions (returns String[])")
    public ResultTask<List<PermissionInfo>> queryPermissionsByGroup (String permissionGroup, int flags) {
        return new ResultCreator<List<PermissionInfo>>(buildMethodCallPacketData("queryPermissionsByGroup", List.class, permissionGroup, flags)).postMethodProcess(context);
    }

    public ResultTask<PackageInfo> getPackageInfo (String packageName, int flags) {
        return new ResultCreator<PackageInfo>(buildMethodCallPacketData("getPackageInfo", PackageInfo.class, packageName, flags)).postMethodProcess(context);
    }

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

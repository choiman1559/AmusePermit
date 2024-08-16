package com.amuse.permit.wrapper.pkg;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;

import androidx.annotation.NonNull;

import com.amuse.permit.Instance;
import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.model.Annotations;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.model.Wrappable;

import java.util.List;

@SuppressWarnings("unused")
public class QueryPkgNativeWrapper extends QueryPkgModel {

    private PackageManager packageManager;

    public QueryPkgNativeWrapper() {
        //For dynamic instance creation
    }

    @Override
    @Annotations.Constructor
    public ResultTask<Wrappable> createServerInstance(@NonNull Context context, @NonNull ArgsInfo packetData) {
        ResultTask<Wrappable> wrappableResultTask = new ResultTask<>();
        wrappableResultTask.mOnInvokeAttached = result -> {
            ResultTask.Result<QueryPkgNativeWrapper> wrappableResult = new ResultTask.Result<>();
            packageManager = context.getPackageManager();

            wrappableResult.setResultData(this);
            wrappableResult.setSuccess(packageManager != null);
            wrappableResultTask.callCompleteListener(wrappableResult);
        };
        return wrappableResultTask;
    }

    @Annotations.ResponserSide
    public List<ApplicationInfo> getInstalledApplications (Integer flags) {
        return packageManager.getInstalledApplications(flags);
    }

    @Annotations.ResponserSide
    public List<ResolveInfo> queryIntentActivities (Intent intent, Integer flags) {
        return packageManager.queryIntentActivities(intent, flags);
    }

    @Annotations.ResponserSide
    public List<ResolveInfo> queryBroadcastReceivers (Intent intent, Integer flags) {
        return packageManager.queryBroadcastReceivers(intent, flags);
    }

    @Annotations.ResponserSide
    public List<ResolveInfo> queryIntentServices (Intent intent, Integer flags) {
        return packageManager.queryIntentServices(intent, flags);
    }

    @Annotations.ResponserSide
    public List<ResolveInfo> queryIntentContentProviders (Intent intent, Integer flags) {
        return packageManager.queryIntentContentProviders(intent, flags);
    }

    @Annotations.ResponserSide
    public List<InstrumentationInfo> queryInstrumentation (String targetPackage, Integer flags) {
        return packageManager.queryInstrumentation(targetPackage, flags);
    }

    @Deprecated(since = "This function returns blank Array since SDK 33. Instead, Use PackageManager.getPackageInfo and get PackageInfo.requestedPermissions")
    @Annotations.ResponserSide
    public List<PermissionInfo> queryPermissionsByGroup (String permissionGroup, Integer flags) throws PackageManager.NameNotFoundException {
        return packageManager.queryPermissionsByGroup(permissionGroup, flags);
    }

    @Annotations.ResponserSide
    public PackageInfo getPackageInfo (String packageName, Integer flags) throws PackageManager.NameNotFoundException {
        return packageManager.getPackageInfo(packageName, flags);
    }

    @Annotations.ResponserSide
    public Boolean[] canPackageQuery (String sourcePkg, String[] targetPkgs) throws PackageManager.NameNotFoundException {
        boolean[] result = packageManager.canPackageQuery(sourcePkg, targetPkgs);
        Boolean[] finalResult = new Boolean[result.length];
        for(int i = 0; i < finalResult.length; i++) {
            finalResult[i] = result[i];
        }
        return finalResult;
    }
}

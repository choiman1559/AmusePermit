package com.amuse.permit.wrapper.locate;

import android.annotation.SuppressLint;
import android.location.Location;

import com.amuse.permit.Instance;
import com.amuse.permit.model.ResultTask;
import com.google.android.gms.tasks.CancellationTokenSource;

@SuppressWarnings("unused")
public class LocateWork {
    protected static LocateWorker locateWorker;
    public static class LocateWorker {
        @SuppressLint("MissingPermission")
        public ResultTask<Location> onLocationRequested(Integer priority) {
            ResultTask<Location> resultTask = new ResultTask<>();
            ResultTask.Result<Location> voidResult = new ResultTask.Result<>();

            resultTask.mOnInvokeAttached = result -> {
                if(LocateNativeWrapper.fusedLocationProviderClient != null) {
                    LocateNativeWrapper.fusedLocationProviderClient.getCurrentLocation(priority,
                                    new CancellationTokenSource().getToken()).addOnCompleteListener(result1 -> {
                        voidResult.setSuccess(result1.isSuccessful());
                        voidResult.setResultData(result1.getResult());
                        resultTask.callCompleteListener(voidResult);
                    });
                } else {
                    voidResult.setSuccess(false);
                    resultTask.callCompleteListener(voidResult);
                }
            };
            return resultTask;
        }
    }

    public static void registerWorker(LocateWorker locateWorker) {
        LocateWork.locateWorker = locateWorker;
    }

    public static void removeWorker() {
        LocateWork.locateWorker = null;
    }

    protected static ResultTask<Location> runWorker(Integer priority) {
        if(LocateWork.locateWorker == null) {
            Instance.printLog("LocateWork.locateWorker is not initialized, Using default worker instance");
            return new LocateWorker().onLocationRequested(priority);
        } else  {
            return LocateWork.locateWorker.onLocationRequested(priority);
        }
    }
}

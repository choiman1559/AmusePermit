package com.amuse.permit.wrapper.locate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.model.Annotations;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.model.Wrappable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

@SuppressWarnings("unused")
@SuppressLint("MissingPermission")
public class LocateNativeWrapper extends LocateModel {

    protected static FusedLocationProviderClient fusedLocationProviderClient;

    public LocateNativeWrapper() {
        //For dynamic instance creation
    }

    @Override
    @Annotations.Constructor
    public ResultTask<Wrappable> createServerInstance(@NonNull Context context, @NonNull ArgsInfo packetData) {
        ResultTask<Wrappable> resultTask = new ResultTask<>();
        resultTask.mOnInvokeAttached = result -> new Thread(() -> {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                ResultTask.Result<Wrappable> voidResult = new ResultTask.Result<>();
                if(fusedLocationProviderClient == null) {
                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
                    LocateModel locationModel = (LocateModel) packetData.getData(0);

                    if(locationModel.isMock) {
                        fusedLocationProviderClient.setMockMode(true).addOnCompleteListener(result1 -> {
                            if(result1.isSuccessful() && locationModel.mockLocation != null) {
                                fusedLocationProviderClient.setMockLocation(locationModel.mockLocation).addOnCompleteListener(result2 -> {
                                    voidResult.setSuccess(result1.isSuccessful());
                                    voidResult.setResultData(LocateNativeWrapper.this);
                                    resultTask.callCompleteListener(voidResult);
                                });
                            } else {
                                voidResult.setSuccess(result1.isSuccessful());
                                voidResult.setResultData(LocateNativeWrapper.this);
                                resultTask.callCompleteListener(voidResult);
                            }
                        });
                    } else {
                        voidResult.setSuccess(true);
                        voidResult.setResultData(LocateNativeWrapper.this);
                        resultTask.callCompleteListener(voidResult);
                    }
                } else {
                    voidResult.setSuccess(true);
                    voidResult.setResultData(LocateNativeWrapper.this);
                    resultTask.callCompleteListener(voidResult);
                }
            });
        }).start();
        return resultTask;
    }

    @Annotations.ResponserSide
    public ResultTask<Boolean> flushLocations() {
        ResultTask<Boolean> resultTask = new ResultTask<>();
        ResultTask.Result<Boolean> voidResult = new ResultTask.Result<>();

        resultTask.mOnInvokeAttached = result -> {
            if(fusedLocationProviderClient != null) {
                fusedLocationProviderClient.flushLocations().addOnCompleteListener(result1 -> {
                    voidResult.setSuccess(true);
                    voidResult.setResultData(result1.isSuccessful());
                    resultTask.callCompleteListener(voidResult);
                });
            } else {
                voidResult.setSuccess(false);
                voidResult.setResultData(false);
                resultTask.callCompleteListener(voidResult);
            }
        };
        return resultTask;
    }

    @Annotations.ResponserSide
    public ResultTask<Location> getLastLocation() {
        ResultTask<Location> resultTask = new ResultTask<>();
        ResultTask.Result<Location> voidResult = new ResultTask.Result<>();

        resultTask.mOnInvokeAttached = result -> {
            if(fusedLocationProviderClient != null) {
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(result1 -> {
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

    @Annotations.ResponserSide
    public ResultTask<Location> getCurrentLocation(Integer priority) {
        return LocateWork.runWorker(priority);
    }

    @Annotations.ResponserSide
    ResultTask<Boolean> getLocationAvailability() {
        ResultTask<Boolean> resultTask = new ResultTask<>();
        ResultTask.Result<Boolean> voidResult = new ResultTask.Result<>();

        resultTask.mOnInvokeAttached = result -> {
            if(fusedLocationProviderClient != null) {
                fusedLocationProviderClient.getLocationAvailability().addOnCompleteListener(result1 -> {
                    voidResult.setSuccess(result1.isSuccessful());
                    voidResult.setResultData(result1.getResult().isLocationAvailable());
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
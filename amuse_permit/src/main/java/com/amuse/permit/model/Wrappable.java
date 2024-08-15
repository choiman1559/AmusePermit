package com.amuse.permit.model;

import android.content.Context;
import androidx.annotation.NonNull;

import com.amuse.permit.data.ArgsInfo;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;

@SuppressWarnings("unused")
public abstract class Wrappable implements Serializable {

    @JsonIgnore
    protected boolean isFetched = false;

    @JsonSetter("isFetched")
    public void setIsFetched(boolean isFetched) {
        this.isFetched = isFetched;
    }

    @JsonGetter("isFetched")
    public boolean checkIsFetched() {
        return isFetched;
    }

    public boolean checkPermissionGranted(@NonNull Context context) {
        return true;
    }

    public Wrappable createServerInstance(@NonNull ArgsInfo argsInfo) {
        return null;
    }

    public ResultTask<Wrappable> createServerInstance(@NonNull Context context, @NonNull ArgsInfo argsInfo) {
        return null;
    }
}

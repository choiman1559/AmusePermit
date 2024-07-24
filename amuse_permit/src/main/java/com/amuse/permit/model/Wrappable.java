package com.amuse.permit.model;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public abstract class Wrappable implements Serializable {
    @JsonIgnore
    private boolean isFetched = false;

    public void setIsFetched(boolean isFetched) {
        this.isFetched = isFetched;
    }

    protected void checkIsFetched() {
        if(isFetched) {
            throw new IllegalStateException("Not yet fetched");
        }
    }

    public ResultTask<Intent> postRequest(Object... args) {
        return null;
    }

    public Class<?> getNativeImplClass() {
        return null;
    }

    public boolean checkPermissionGranted(@NonNull Context context) {
        return false;
    }

    public String getClassTag() {
        return null;
    }
}

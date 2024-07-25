package com.amuse.permit.model;

import android.content.Context;
import androidx.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

@SuppressWarnings("unused")
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

    public boolean checkPermissionGranted(@NonNull Context context) {
        return false;
    }
}

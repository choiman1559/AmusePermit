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

    /**
     * Set if the data is fetched from server application,
     * used in the internal implementation of AmusePermit.
     */
    @JsonSetter("isFetched")
    public void setIsFetched(boolean isFetched) {
        this.isFetched = isFetched;
    }

    /**
     * Check if the data is fetched from server application
     * used in the internal implementation of AmusePermit.
     *
     * @return true if the data is fetched from server application
     */
    @JsonGetter("isFetched")
    public boolean checkIsFetched() {
        return isFetched;
    }

    /**
     * Check if required permissions to get data from server application is granted,
     * used in the internal implementation of AmusePermit.
     *
     * @return true if the required permissions are granted
     */
    public boolean checkPermissionGranted(@NonNull Context context) {
        return true;
    }

    /**
     * Creates a new instance of this specific API class instance,
     * used in the internal implementation of AmusePermit of wrapper classes.
     *
     * @return a new instance of API wrapper class
     */
    public Wrappable createServerInstance(@NonNull ArgsInfo argsInfo) {
        return null;
    }

    /**
     * Creates a new instance of this specific API class instance asynchronously,
     * used in the internal implementation of AmusePermit of wrapper classes.
     *
     * @return a new instance of API wrapper class
     */
    public ResultTask<Wrappable> createServerInstance(@NonNull Context context, @NonNull ArgsInfo argsInfo) {
        return null;
    }
}

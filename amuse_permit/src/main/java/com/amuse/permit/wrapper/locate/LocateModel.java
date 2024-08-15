package com.amuse.permit.wrapper.locate;

import android.location.Location;
import androidx.annotation.IntDef;

import com.amuse.permit.model.Wrappable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class LocateModel extends Wrappable {
    private static final long serialVersionUID = 314159265L;

    @JsonIgnore
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.TYPE_USE, ElementType.PARAMETER, ElementType.TYPE})
    @IntDef({LocateModel.PRIORITY_BALANCED_POWER_ACCURACY, LocateModel.PRIORITY_HIGH_ACCURACY, LocateModel.PRIORITY_LOW_POWER, LocateModel.PRIORITY_PASSIVE})
    public @interface LocationPriority { }

    @JsonIgnore
    public static final int PRIORITY_BALANCED_POWER_ACCURACY = 102;
    @JsonIgnore
    public static final int PRIORITY_HIGH_ACCURACY = 100;
    @JsonIgnore
    public static final int PRIORITY_LOW_POWER = 104;
    @JsonIgnore
    public static final int PRIORITY_PASSIVE = 105;

    protected boolean isMock;
    protected Location mockLocation;
}

package com.amuse.permit.wrapper.cursor;

import android.net.Uri;

import androidx.annotation.Nullable;

@SuppressWarnings("unused")
public abstract class ContentObserver {

    protected String ticketId;

    public void onChange(Boolean isSelfChanged) {

    }

    public void onChange(Boolean isSelfChanged, @Nullable Uri uri) {

    }
}

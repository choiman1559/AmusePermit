package com.amuse.permit.wrapper.cursor;

import android.net.Uri;

@SuppressWarnings("unused")
public abstract class ContentObserver {

    protected String ticketId;

    public void onChange(Boolean isSelfChanged) {

    }

    public void onChange(Boolean isSelfChanged, Uri uri) {

    }
}

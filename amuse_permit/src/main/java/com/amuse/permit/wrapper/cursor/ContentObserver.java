package com.amuse.permit.wrapper.cursor;

import android.net.Uri;

import androidx.annotation.Nullable;

/**
 * Receives call backs for changes to content.
 *
 * @see android.database.ContentObserver
 */
@SuppressWarnings("unused")
public abstract class ContentObserver {

    protected String ticketId;

    public void onChange(Boolean isSelfChanged) {

    }

    public void onChange(Boolean isSelfChanged, @Nullable Uri uri) {

    }
}

package com.amuse.permit.wrapper.cursor;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.process.ProcessConst;
import com.amuse.permit.process.action.ServerAction;

public class ObserverWrapper extends ContentObserver {

    protected Context context;
    protected PacketData packetData;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public ObserverWrapper(Context context, Handler handler, PacketData packetData) {
        super(handler);
        this.context = context;
        this.packetData = packetData;
    }

    @Override
    public void onChange(boolean selfChange, @Nullable Uri uri) {
        ArgsInfo argsInfo = new ArgsInfo();
        argsInfo.put(ProviderManager.CALL_OBSERVER);
        argsInfo.put(Boolean.class, selfChange);
        argsInfo.put(Uri.class, ProcessConst.KEY_PARCEL_LIST_REPLACED);
        packetData.parcelableList.add(uri);

        new ServerAction(context, packetData)
                .pushStream(ProcessConst.ACTION_TYPE_CURSOR, argsInfo, packetData.parcelableList)
                .send();
    }

    @Override
    public void onChange(boolean selfChange) {
        onChange(selfChange, null);
    }
}

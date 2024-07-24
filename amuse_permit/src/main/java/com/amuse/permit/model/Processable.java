package com.amuse.permit.model;

import android.content.Context;
import android.os.Bundle;

public interface Processable {
    String getType();
    void onPacketReceived(Context context, Bundle bundle) throws Exception;
}

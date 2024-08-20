package com.amuse.permit.model;

import android.content.Context;
import android.os.Bundle;

public interface Processable {
    @Annotations.ApiTypes String getType();
    void onPacketReceived(Context context, Bundle bundle) throws Exception;
    Class<?> getNativeImplClass();
}

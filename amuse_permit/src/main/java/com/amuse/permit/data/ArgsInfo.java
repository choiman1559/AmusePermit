package com.amuse.permit.data;

import androidx.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class ArgsInfo implements Serializable {
    private final ArrayList<Class<?>> clsArrays;
    private final ArrayList<Object> dataArrays;

    public ArgsInfo() {
        this.clsArrays = new ArrayList<>();
        this.dataArrays = new ArrayList<>();
    }

    public void put(@Nullable Object data) {
        clsArrays.add(data == null ? Object.class : data.getClass());
        dataArrays.add(data);
    }

    public void put(Class<?> cls, Object data) {
        clsArrays.add(cls);
        dataArrays.add(data);
    }

    public void set(int index, Class<?> cls, Object data) {
        clsArrays.set(index, cls);
        dataArrays.set(index, data);
    }

    public Class<?> getCls(int index) {
        return clsArrays.get(index);
    }

    public Object getData(int index) {
        return dataArrays.get(index);
    }

    public int size() {
        return dataArrays.size();
    }
}

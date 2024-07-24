package com.amuse.permit.model;

import java.io.Serializable;

public class NameFilters {
    public interface NameFilter<T> extends Serializable {
        boolean accept(T object);
    }

    public interface NameFilterWithExtra<T, E> extends Serializable {
        boolean accept(T object, E extra);
    }
}

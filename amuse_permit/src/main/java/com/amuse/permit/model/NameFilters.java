package com.amuse.permit.model;

import java.io.Serializable;

public class NameFilters {

    /**
     * Interface for filtering specific objects.
     */
    public interface NameFilter<T> extends Serializable {
        /**
         * Get server application information that previously set.
         *
         * @param object The object to filter on, usually String type
         * @return return true if accept object, or false if not accept
         */
        boolean accept(T object);
    }

    /**
     * Interface for filtering specific objects with extra information.
     */
    public interface NameFilterWithExtra<T, E> extends Serializable {
        /**
         * Get server application information that previously set.
         *
         * @param object The object to filter on, usually String type
         * @param extra An object to provide additional information to filter on.
         * @return return true if accept object, or false if not accept
         */
        boolean accept(T object, E extra);
    }
}

package com.amuse.permit.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Annotations {
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PACKAGE})
    public @interface RequesterSide {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PACKAGE})
    public @interface ResponserSide {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PACKAGE})
    public @interface StaticMethod {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PACKAGE})
    public @interface Constructor {
    }
}

package com.amuse.permit.model;

import androidx.annotation.StringDef;

import com.amuse.permit.process.ProcessConst;

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
    @Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PACKAGE})
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

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.PACKAGE})
    public @interface NativeWrapper {
    }

    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.TYPE_USE, ElementType.PARAMETER, ElementType.TYPE})
    @StringDef({ProcessConst.ACTION_TYPE_FILE, ProcessConst.ACTION_TYPE_LOCATION, ProcessConst.ACTION_TYPE_PACKAGE, ProcessConst.ACTION_TYPE_TELEPHONY})
    public @interface ApiTypes { }
}

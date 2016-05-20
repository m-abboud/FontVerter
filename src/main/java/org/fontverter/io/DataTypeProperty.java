package org.fontverter.io;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface DataTypeProperty {
    enum DataType {
        SHORT, USHORT, LONG, ULONG, FIXED32, INT, UINT, STRING, BYTE_ARRAY, LONG_DATE_TIME, UINT_BASE_128;
    }

    DataType dataType();

    String ignoreIf() default "";

    boolean ignore() default false;

    int order() default -1;

    int byteLength() default -1;
}

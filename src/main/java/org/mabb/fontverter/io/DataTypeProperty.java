package org.mabb.fontverter.io;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface DataTypeProperty {
    enum DataType {
        SHORT,
        USHORT,
        LONG,
        ULONG,
        FIXED32,
        INT,
        UINT,
        STRING,
        LONG_DATE_TIME,
        UINT_BASE_128,
        BYTE_ARRAY,
        PASCAL_STRING
    }

    DataType dataType();

    String ignoreIf() default "";

    String includeIf() default "";

    boolean ignore() default false;

    int order() default -1;

    int byteLength() default -1;

    String arrayLength() default "";

    boolean isArray() default false;
}

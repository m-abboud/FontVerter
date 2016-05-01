package org.fontverter.opentype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface OpenTypeProperty
{
    public enum DataType
    {
        SHORT, USHORT, LONG, ULONG, FIXED32, INT, STRING, BYTE_ARRAY, LONGDATETIME
    }

    DataType dataType();
}

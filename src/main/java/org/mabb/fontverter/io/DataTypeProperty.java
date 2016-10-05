/*
 * Copyright (C) Maddie Abboud 2016
 *
 * FontVerter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FontVerter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FontVerter. If not, see <http://www.gnu.org/licenses/>.
 */

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
        BYTE,
        STRING,
        LONG_DATE_TIME,
        UINT_BASE_128,
        BYTE_ARRAY,
        PASCAL_STRING;
    }

    DataType dataType();

    String ignoreIf() default "";

    String includeIf() default "";

    boolean ignore() default false;

    int order() default -1;

    int constLength() default -1;

    String arrayLength() default "";

    boolean isArray() default false;
}

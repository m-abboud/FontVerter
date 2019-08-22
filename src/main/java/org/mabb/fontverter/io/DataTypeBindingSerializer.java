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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class DataTypeBindingSerializer {
    private FontDataOutput writer;
    private DataTypeAnnotationReader propReader = new DataTypeAnnotationReader();

    public byte[] serialize(Object object) throws DataTypeSerializerException {
        return serialize(object, new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET));
    }
    public byte[] serialize(Object object, FontDataOutput writer) throws DataTypeSerializerException {
        this.writer = writer;
        Class<?> type = object.getClass();
        List<AccessibleObject> properties = propReader.getProperties(type);

        for (AccessibleObject propertyOn : properties) {
            try {
                serializeProperty(object, propertyOn);
            } catch (Exception e) {
                throw new DataTypeSerializerException("Error serializing property: " + propertyOn.toString(), e);
            }
        }

        try {
            writer.flush();
        } catch (IOException ex) {
            throw new DataTypeSerializerException(ex);
        }

        return writer.toByteArray();
    }

    private void serializeProperty(Object object, AccessibleObject propertyOn)
            throws Exception {
        if (!propertyOn.isAnnotationPresent(DataTypeProperty.class))
            return;

        Annotation annotation = propertyOn.getAnnotation(DataTypeProperty.class);
        DataTypeProperty property = (DataTypeProperty) annotation;
        if (propReader.isIgnoreProperty(property, object))
            return;

        Object propValue;
        if (propertyOn instanceof Method)
            propValue = ((Method) propertyOn).invoke(object);
        else if (propertyOn instanceof Field)
            propValue = ((Field) propertyOn).get(object);
        else
            throw new DataTypeSerializerException("Byte property binding on unknown type");

        if (property.isArray())
            writeArrayValue((Object[]) propValue, property, object);
        else
            writeValue(property, propValue);
    }

    private void writeValue(DataTypeProperty property, Object fieldValue) throws IOException {
        switch (property.dataType()) {
            case SHORT:
                writer.writeShort(((Number) fieldValue).shortValue());
                break;
            case USHORT:
                writer.writeUnsignedShort(((Number) fieldValue).intValue());
                break;
            // note (U)LONG = 32 bits in true/open type, dunno if should change name to like UINT32 instead or if more confusing
            case LONG:
                writer.writeInt(((Number) fieldValue).intValue());
                break;
            case ULONG:
                writer.writeUnsignedInt((int) ((Number) fieldValue).longValue());
                break;
            case FIXED32:
                writer.write32Fixed(((Number) fieldValue).floatValue());
                break;
            case UINT:
            case INT:
                writer.writeInt(((Number) fieldValue).intValue());
                break;
            case STRING:
                writer.writeString((String) fieldValue);
                break;
            case BYTE_ARRAY:
                writer.write((byte[]) fieldValue);
                break;
            case LONG_DATE_TIME:
                Calendar date = (Calendar) fieldValue;
                writer.writeLong(date.getTimeInMillis() / 1000);
                break;
            case PASCAL_STRING:
                String value = ((String) fieldValue);
                writer.writeByte(value.length());
                writer.writeString(value);
                break;
            case BYTE:
                writer.writeByte((Byte) fieldValue);
                break;
            case UINT_BASE_128:
                throw new IOException("Data type annotation serialization is not implemented for type: " +
                        property.dataType());
        }
    }

    private void writeArrayValue(Object[] array, DataTypeProperty binding, Object object)
            throws NoSuchFieldException, IllegalAccessException, IOException, InvocationTargetException {
        int arrayLength = propReader.getPropertyArrayLength(binding, object);
        if (arrayLength < 0)
            throw new IOException("Array length must be set for array data types.");

        for (int i = 0; i < arrayLength; i++)
            writeValue(binding, array[i]);
    }
}


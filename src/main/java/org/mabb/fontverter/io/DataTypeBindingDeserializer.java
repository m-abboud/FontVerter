/*
 * Copyright (C) Matthew Abboud 2016
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DataTypeBindingDeserializer {
    private static Logger log = LoggerFactory.getLogger(DataTypeBindingDeserializer.class);

    private DataTypeAnnotationReader propReader = new DataTypeAnnotationReader();
    private FontDataInputStream input;
    private boolean recoverFromEOF;
    private boolean stopDeserializeEarly = false;

    public Object deserialize(byte[] data, Class toClass) throws DataTypeSerializerException {
        return deserialize(new FontDataInputStream(data), toClass);
    }

    public Object deserialize(FontDataInputStream dataInput, Class toClass) throws DataTypeSerializerException {
        try {
            return deserialize(dataInput, toClass.newInstance());
        } catch (Exception ex) {
            throw new DataTypeSerializerException(ex);
        }
    }

    public Object deserialize(FontDataInputStream dataInput, Object toObj) throws DataTypeSerializerException {
        try {
            input = dataInput;

            Class toClass = toObj.getClass();
            List<AccessibleObject> properties = propReader.getProperties(toClass);

            for (AccessibleObject propertyOn : properties) {
                if (stopDeserializeEarly)
                    break;

                try {
                    deserializeProperty(propertyOn, toObj);
                } catch (Exception ex) {
                    throw new DataTypeSerializerException(propertyOn.toString() + " " + toObj.getClass().getCanonicalName(), ex);
                }
            }

            return toObj;
        } catch (Exception ex) {
            throw new DataTypeSerializerException(toObj.getClass().getCanonicalName(), ex);
        }
    }

    public Object deserialize(byte[] data, Object toObj) throws DataTypeSerializerException {
        return deserialize(new FontDataInputStream(data), toObj);
    }

    private void deserializeProperty(AccessibleObject propertyOn, Object object) throws Exception {
        if (!propertyOn.isAnnotationPresent(DataTypeProperty.class))
            return;

        Annotation annotation = propertyOn.getAnnotation(DataTypeProperty.class);
        DataTypeProperty binding = (DataTypeProperty) annotation;
        if (propReader.isIgnoreProperty(binding, object))
            return;

        Object inValue = null;
        if (binding.isArray()) {
            inValue = readArrayValue((Field) propertyOn, object, binding);
        } else
            inValue = readSingleValue(binding);

        if (propertyOn instanceof Field)
            ((Field) propertyOn).set(object, inValue);
        else
            throw new IOException("Method property deserialization not implemented" + propertyOn.toString());
    }

    private Object readSingleValue(DataTypeProperty property) throws IOException {
        switch (property.dataType()) {
            case SHORT:
                return input.readShort();
            case USHORT:
                return input.readUnsignedShort();
            case LONG:
                return input.readInt();
            case ULONG:
                return input.readUnsignedInt();
            case FIXED32:
                return input.readFixed32();
            case INT:
                return input.readInt();
            case UINT:
                return input.readUnsignedInt();
            case STRING:
                return input.readString(property.byteLength());
            case BYTE_ARRAY:
                return input.readBytes(property.byteLength());
            case LONG_DATE_TIME:
                Calendar date = Calendar.getInstance();
                date.setTimeInMillis(input.readLong() * 1000);
                return date;
            case UINT_BASE_128:
                return input.readUIntBase128();
            case PASCAL_STRING:
                int strLength = input.readUnsignedByte();
                return input.readString(strLength);
        }

        throw new IOException("Deserialize not implemented for peroperty type: " + property.dataType());
    }

    private Object readArrayValue(Field propertyOn, Object object, DataTypeProperty binding) throws NoSuchFieldException, IllegalAccessException, IOException, InvocationTargetException {
        int arrayLength = propReader.getPropertyArrayLength(binding, object);
        if (arrayLength < 0)
            throw new IOException("Array length must be set for array data types.");

        Object[] array = (Object[]) Array.newInstance(propertyOn.getType().getComponentType(), arrayLength);

        for (int i = 0; i < arrayLength; i++) {
            try {
                array[i] = readSingleValue(binding);
            } catch (Exception ex) {
                String message = String.format("Array length ran over input data length." +
                        " Index on: %d Array Length: %d", i, arrayLength);

                if (isRecoverFromEOF()) {
                    stopDeserializeEarly = true;
                    log.debug(message);
                    return array;
                }

                throw new IOException(message);
            }
        }

        return array;
    }

    public void setRecoverFromEOF(boolean recoverFromEOF) {
        this.recoverFromEOF = recoverFromEOF;
    }

    public boolean isRecoverFromEOF() {
        return recoverFromEOF;
    }
}


package org.mabb.fontverter.io;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class DataTypeBindingSerializer {
    private FontDataOutputStream writer;
    private DataTypeAnnotationReader propReader = new DataTypeAnnotationReader();

    public byte[] serialize(Object object) throws DataTypeSerializerException {
        writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET);
        Class type = object.getClass();
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
            throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
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
        }
    }
}


package org.fontverter.io;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ByteBindingSerializer {
    private ByteDataOutputStream writer;
    private ByteBindingsReader propReader = new ByteBindingsReader();

    public byte[] serialize(Object object) throws ByteSerializerException {
        try {
            writer = new ByteDataOutputStream(ByteDataOutputStream.OPEN_TYPE_CHARSET);
            Class type = object.getClass();
            List<AccessibleObject> properties = propReader.getProperties(type);

            try {
                for (AccessibleObject propertyOn : properties)
                    serializeProperty(object, propertyOn);
            } catch (Exception e) {
                throw new ByteSerializerException(e);
            }

            writer.flush();
        } catch (IOException ex) {
            throw new ByteSerializerException(ex);
        }
        return writer.toByteArray();
    }

    private void serializeProperty(Object object, AccessibleObject propertyOn) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (!propertyOn.isAnnotationPresent(ByteDataProperty.class))
            return;

        propertyOn.setAccessible(true);
        Annotation annotation = propertyOn.getAnnotation(ByteDataProperty.class);
        ByteDataProperty property = (ByteDataProperty) annotation;
        if (propReader.isIgnoreProperty(property, object))
            return;

        Object propValue;
        if (propertyOn instanceof Method)
            propValue = ((Method) propertyOn).invoke(object);
        else if (propertyOn instanceof Field)
            propValue = ((Field) propertyOn).get(object);
        else
            throw new ByteSerializerException("Byte property binding on unknown type");

        writeValue(property, propValue);
    }

    private void writeValue(ByteDataProperty property, Object fieldValue) throws IOException {
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


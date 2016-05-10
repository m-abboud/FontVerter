package org.fontverter.io;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ByteBindingSerializer {
    private ByteDataOutputStream writer = new ByteDataOutputStream(ByteDataOutputStream.OPEN_TYPE_CHARSET);

    public byte[] serialize(Object object) throws ByteSerializerException {
        try {
            Class type = object.getClass();
            List<Object> properties = getProperties(type);

            for (Object propertyOn : properties)
                serializeProperty(object, propertyOn);

            writer.flush();
        } catch (IOException ex) {
            throw new ByteSerializerException(ex);
        }
        return writer.toByteArray();
    }

    private void serializeProperty(Object object, Object propertyOn) throws ByteSerializerException {
        try {
            if (propertyOn instanceof Method)
                serializeMethod(object, (Method) propertyOn);
            else if (propertyOn instanceof Field)
                serializeField(object, (Field) propertyOn);
        } catch (Exception e) {
            throw new ByteSerializerException(e);
        }
    }

    private List<Object> getProperties(Class type) throws ByteSerializerException {
        List<Object> properties = new LinkedList<Object>();

        for (Field fieldOn : type.getDeclaredFields()) {
            if (fieldOn.isAnnotationPresent(ByteDataProperty.class))
                properties.add(fieldOn);
        }
        for (Method methodOn : type.getDeclaredMethods()) {
            if (methodOn.isAnnotationPresent(ByteDataProperty.class))
                properties.add(methodOn);
        }

        sortProperties(properties);

        return properties;
    }

    private void sortProperties(List<Object> properties) {
        Collections.sort(properties, new Comparator<Object>() {
            public int compare(Object obj1, Object obj2) {
                try {
                    int order1 = getPropertyAnnotation(obj1).order();
                    int order2 = getPropertyAnnotation(obj2).order();

                    return order1 < order2 ? -1 : order1 == order2 ? 0 : 1;
                } catch (ByteSerializerException e) {
                    return 0;
                }
            }
        });
    }

    private ByteDataProperty getPropertyAnnotation(Object property) throws ByteSerializerException {
        if (property instanceof Field)
            return ((Field) property).getAnnotation(ByteDataProperty.class);
        else if (property instanceof Method)
            return ((Method) property).getAnnotation(ByteDataProperty.class);

        throw new ByteSerializerException("Could not find annotation for property " + property.toString());
    }

    private void serializeMethod(Object object, Method method) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (!method.isAnnotationPresent(ByteDataProperty.class))
            return;

        method.setAccessible(true);
        Annotation annotation = method.getAnnotation(ByteDataProperty.class);
        ByteDataProperty property = (ByteDataProperty) annotation;
        if (isIgnoreProperty(property, object))
            return;

        Object retValue = method.invoke(object);
        writeValue(property, retValue);
    }

    private void serializeField(Object object, Field field)
            throws IllegalAccessException, IOException, NoSuchMethodException, InvocationTargetException {
        if (!field.isAnnotationPresent(ByteDataProperty.class))
            return;

        field.setAccessible(true);
        Annotation annotation = field.getAnnotation(ByteDataProperty.class);
        ByteDataProperty property = (ByteDataProperty) annotation;
        if (isIgnoreProperty(property, object))
            return;

        Object fieldValue = field.get(object);
        writeValue(property, fieldValue);
    }

    private boolean isIgnoreProperty(ByteDataProperty property, Object object)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (property.ignoreIf().isEmpty())
            return false;

        Method method = object.getClass().getMethod(property.ignoreIf());
        method.setAccessible(true);
        return (Boolean) method.invoke(object);
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
            case LONGDATETIME:
                Calendar date = (Calendar) fieldValue;
                writer.writeLong(date.getTimeInMillis() / 1000);
                break;
        }
    }
}


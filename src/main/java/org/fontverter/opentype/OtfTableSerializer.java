package org.fontverter.opentype;

import org.fontverter.FontWriter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

class OtfTableSerializer {
    private FontWriter writer = FontWriter.createWriter();

    public byte[] serialize(Object object) throws FontSerializerException {
        try {
            Class type = object.getClass();
            List<Object> properties = getOrderedProperties(type);

            for (Object propertyOn : properties)
                serializeProperty(object, propertyOn);

            writer.flush();
        } catch(IOException ex) {
            throw new FontSerializerException(ex);
        }
        return writer.toByteArray();
    }

    private void serializeProperty(Object object, Object propertyOn) throws FontSerializerException {
        try {
            if(propertyOn instanceof Method)
                serializeMethod(object, (Method) propertyOn);
            else if(propertyOn instanceof Field)
                serializeField(object, (Field) propertyOn);
        } catch (Exception e) {
            throw new FontSerializerException(e);
        }
    }

    private List<Object> getOrderedProperties(Class type) throws FontSerializerException {
        List<Object> properties = new LinkedList<Object>();

        for (Field fieldOn : type.getDeclaredFields()) {
            if(fieldOn.isAnnotationPresent(OtfDataProperty.class))
                properties.add(fieldOn);
        }
        for (Method methodOn : type.getDeclaredMethods()) {
            if(methodOn.isAnnotationPresent(OtfDataProperty.class))
                properties.add(methodOn);
        }

        Collections.sort(properties, new Comparator<Object>() {
            @Override
            public int compare(Object obj1, Object obj2) {
                try {
                    int order1 = getPropertyAnnotation(obj1).order();
                    int order2 = getPropertyAnnotation(obj2).order();

                    return order1 < order2 ? -1 : order1 == order2 ? 0 : 1;
                } catch (FontSerializerException e) {
                    return 0;
                }
            }
        });

        return properties;
    }

    private OtfDataProperty getPropertyAnnotation(Object property) throws FontSerializerException {
        if(property instanceof Field)
            return ((Field)property).getAnnotation(OtfDataProperty.class);
        else if(property instanceof Method)
            return ((Method)property).getAnnotation(OtfDataProperty.class);
        throw new FontSerializerException("Could not find annotation for property " + property.toString());
    }

    private void serializeMethod(Object object, Method method) throws IOException, InvocationTargetException, IllegalAccessException {
        if (!method.isAnnotationPresent(OtfDataProperty.class))
            return;

        method.setAccessible(true);
        Annotation annotation = method.getAnnotation(OtfDataProperty.class);
        OtfDataProperty property = (OtfDataProperty) annotation;
        Object retValue = method.invoke(object);
        switch (property.dataType()) {
            case SHORT:
                writer.writeShort((Integer) retValue);
                break;
            case USHORT:
                writer.writeUnsignedShort((Integer) retValue);
                break;
            case LONG:
                writer.writeInt((Integer) retValue);
                break;
            case ULONG:
                writer.writeUnsignedInt((Integer) retValue);
                break;
            case FIXED32:
                writer.write32Fixed((Float) retValue);
                break;
            case INT:
                writer.writeInt((Integer) retValue);
                break;
            case STRING:
                writer.writeString((String) retValue);
                break;
            case BYTE_ARRAY:
                writer.write((byte[]) retValue);
                break;
            case LONGDATETIME:
                Calendar date = (Calendar) retValue;
                writer.writeLong((long) (date.getTimeInMillis() / 1000));
                break;
        }
    }

    private void serializeField(Object object, Field field) throws IllegalAccessException, IOException {
        if (!field.isAnnotationPresent(OtfDataProperty.class))
            return;

        field.setAccessible(true);
        Annotation annotation = field.getAnnotation(OtfDataProperty.class);
        OtfDataProperty property = (OtfDataProperty) annotation;
        Object fieldValue = field.get(object);
        switch (property.dataType()) {
            case SHORT:
                writer.writeShort(field.getShort(object));
                break;
            case USHORT:
                writer.writeUnsignedShort(field.getInt(object));
                break;
            case LONG:
                writer.writeInt(field.getInt(object));
                break;
            case ULONG:
                writer.writeUnsignedInt((int) field.getLong(object));
                break;
            case FIXED32:
                writer.write32Fixed(field.getFloat(object));
                break;
            case INT:
                writer.writeInt(field.getInt(object));
                break;
            case STRING:
                writer.writeString((String) fieldValue);
                break;
            case BYTE_ARRAY:
                writer.write((byte[]) fieldValue);
                break;
            case LONGDATETIME:
                Calendar date = (Calendar) fieldValue;
                writer.writeLong((long) (date.getTimeInMillis() / 1000));
                break;
        }
    }


}


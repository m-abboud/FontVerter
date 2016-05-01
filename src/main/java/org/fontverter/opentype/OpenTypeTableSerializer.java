package org.fontverter.opentype;

import org.fontverter.FontWriter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;

class OpenTypeTableSerializer
{
    private FontWriter writer = FontWriter.createWriter();
    public byte[] serialize(Object object) throws IOException
    {
        Class type = object.getClass();
        for (Field field : type.getDeclaredFields())
        {
            try
            {
                serializeField(object, field);
            } catch (IllegalAccessException e)
            {
                throw new IOException(e);
            }
        }
        for (Method method: type.getDeclaredMethods())
        {
            try
            {
                serializeMethod(object, method);
            } catch (Exception e)
            {
                throw new IOException(e);
            }
        }
        writer.flush();

        // spec states table data must be multiples of 4, add padding if not
        if(writer.size() % 4 != 0)
            writer.write(new byte[]{0,0});

        return writer.toByteArray();
    }

    private void serializeMethod(Object object, Method method) throws IOException, InvocationTargetException, IllegalAccessException
    {
        if (!method.isAnnotationPresent(OpenTypeProperty.class))
            return;

        method.setAccessible(true);
        Annotation annotation = method.getAnnotation(OpenTypeProperty.class);
        OpenTypeProperty property = (OpenTypeProperty) annotation;
        Object retValue = method.invoke(object);
        switch(property.dataType()) {
            case SHORT:
                writer.writeShort((Integer) retValue);
                break;
            case USHORT:
                writer.writeUnsignedShort((Integer)retValue);
                break;
            case LONG:
                writer.writeInt((Integer)retValue);
                break;
            case ULONG:
                writer.writeUnsignedInt((Integer)retValue);
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


    private void serializeField(Object object, Field field) throws IllegalAccessException, IOException
    {
        if (!field.isAnnotationPresent(OpenTypeProperty.class))
            return;

        field.setAccessible(true);
        Annotation annotation = field.getAnnotation(OpenTypeProperty.class);
        OpenTypeProperty property = (OpenTypeProperty) annotation;
        Object fieldValue = field.get(object);
        switch(property.dataType()) {
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


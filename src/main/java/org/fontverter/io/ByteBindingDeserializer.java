package org.fontverter.io;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.*;

public class ByteBindingDeserializer {
    private ByteBindingsReader propReader = new ByteBindingsReader();
    private ByteDataInputStream reader;

    public Object deserialize(byte[] data, Class toClass) throws ByteSerializerException {
        try {
            reader = new ByteDataInputStream(data);
            List<AccessibleObject> properties = propReader.getProperties(toClass);

            Object outObj = toClass.newInstance();
            for (AccessibleObject propertyOn : properties)
                deserializeProperty(propertyOn, outObj);

            return outObj;
        } catch (Exception ex) {
            throw new ByteSerializerException(ex);
        }
    }

    private void deserializeProperty(AccessibleObject propertyOn, Object object) throws Exception {
        if (!propertyOn.isAnnotationPresent(ByteDataProperty.class))
            return;

        propertyOn.setAccessible(true);
        Annotation annotation = propertyOn.getAnnotation(ByteDataProperty.class);
        ByteDataProperty binding = (ByteDataProperty) annotation;
        if (propReader.isIgnoreProperty(binding, object))
            return;

        Object inValue = readValue(binding);
        if(propertyOn instanceof Field)
            ((Field)propertyOn).set(object, inValue);
        else
            throw new IOException("Method property deserialization not implemented");
    }

    private Object readValue(ByteDataProperty property) throws IOException {
        switch (property.dataType()) {
            case SHORT:
                return reader.readShort();
            case USHORT:
                return reader.readUnsignedShort();
            case LONG:
                return reader.readLong();
            case ULONG:
                return reader.readLong();
            case FIXED32:
                break;
            case INT:
                return reader.readInt();
            case UINT:
                return reader.readUnsignedInt();
            case STRING:
                return reader.readLong();
            case BYTE_ARRAY:
                return reader.readLong();
            case LONG_DATE_TIME:
                Calendar date = Calendar.getInstance();
                date.setTimeInMillis(reader.readLong() * 1000);
                return date;
            case UINT_BASE_128:
                return reader.readUIntBase128();
        }
        throw new IOException("deserialize not implemented");
    }
}


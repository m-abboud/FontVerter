package org.fontverter.io;

import org.fontverter.opentype.OpenTypeTable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.*;

public class DataTypeBindingDeserializer {
    private DataTypeBindingsReader propReader = new DataTypeBindingsReader();
    private ByteDataInputStream input;

    public Object deserialize(byte[] data, Class toClass) throws DataTypeSerializerException {
        return deserialize(new ByteDataInputStream(data), toClass);
    }

    public Object deserialize(ByteDataInputStream dataInput, Class toClass) throws DataTypeSerializerException {
        try {
            return deserialize(dataInput, toClass.newInstance());
        } catch (Exception ex) {
            throw new DataTypeSerializerException(ex);
        }
    }

    public Object deserialize(ByteDataInputStream dataInput, Object toObj) throws DataTypeSerializerException {
        try {
            Class toClass = toObj.getClass();
            input = dataInput;
            List<AccessibleObject> properties = propReader.getProperties(toClass);

            Object outObj = toClass.newInstance();
            for (AccessibleObject propertyOn : properties) {
                try {
                    deserializeProperty(propertyOn, outObj);
                } catch (Exception ex) {
                    throw new DataTypeSerializerException(propertyOn.toString() + " " + toObj.getClass().getCanonicalName(), ex);
                }
            }

            return outObj;
        } catch (Exception ex) {
            throw new DataTypeSerializerException(toObj.getClass().getCanonicalName(), ex);
        }
    }

    public Object deserialize(byte[] data, Object toObj) throws DataTypeSerializerException {
        return deserialize(new ByteDataInputStream(data), toObj);
    }

    private void deserializeProperty(AccessibleObject propertyOn, Object object) throws Exception {
        if (!propertyOn.isAnnotationPresent(DataTypeProperty.class))
            return;

        propertyOn.setAccessible(true);
        Annotation annotation = propertyOn.getAnnotation(DataTypeProperty.class);
        DataTypeProperty binding = (DataTypeProperty) annotation;
        if (propReader.isIgnoreProperty(binding, object))
            return;

        Object inValue = readValue(binding);
        if (propertyOn instanceof Field)
            ((Field) propertyOn).set(object, inValue);
        else
            throw new IOException("Method property deserialization not implemented" + propertyOn.toString());
    }

    private Object readValue(DataTypeProperty property) throws IOException {
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
        }

        throw new IOException("deserialize not implemented");
    }

}


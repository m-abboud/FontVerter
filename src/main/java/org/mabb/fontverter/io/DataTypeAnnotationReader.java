package org.mabb.fontverter.io;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static org.mabb.fontverter.io.DataTypeProperty.DataType.BYTE_ARRAY;
import static org.mabb.fontverter.io.DataTypeProperty.DataType.STRING;

class DataTypeAnnotationReader {
    public DataTypeAnnotationReader() {
    }

    public List<AccessibleObject> getProperties(Class type) throws DataTypeSerializerException {
        List<AccessibleObject> properties = new LinkedList<AccessibleObject>();

        for (Field fieldOn : type.getDeclaredFields()) {
            if (fieldOn.isAnnotationPresent(DataTypeProperty.class)) {
                fieldOn.setAccessible(true);
                properties.add(fieldOn);
            }
        }
        for (Method methodOn : type.getDeclaredMethods()) {
            if (methodOn.isAnnotationPresent(DataTypeProperty.class)) {
                methodOn.setAccessible(true);
                properties.add(methodOn);
            }
        }

        for (AccessibleObject propertyOn : properties) {
            DataTypeProperty annotationOn = getPropertyAnnotation(propertyOn);

            boolean isVarLengthType = annotationOn.dataType() == BYTE_ARRAY || annotationOn.dataType() == STRING;
            if (isVarLengthType && annotationOn.byteLength() < 1)
                throw new DataTypeSerializerException("byteLength annotation field is required for " +
                        annotationOn.dataType());
        }

        sortProperties(properties);
        return properties;
    }


    /* should be called during (de)serialization rather than in getProperties so
     read fields can be used by ignore methods */
    public boolean isIgnoreProperty(DataTypeProperty property, Object object)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String ignoreIf = property.ignoreIf();
        if (!ignoreIf.isEmpty())
            return runIgnoreFilter(object, ignoreIf);

        String includeIf = property.includeIf();
        if (!includeIf.isEmpty())
            return !runIgnoreFilter(object, includeIf);

        return false;
    }

    private boolean runIgnoreFilter(Object object, String ignoreIf) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (ignoreIf.isEmpty())
            return false;

        boolean hasNotOperator = false;
        if (ignoreIf.startsWith("!")) {
            hasNotOperator = true;
            ignoreIf = ignoreIf.replace("!", "");
        }

        Method method = object.getClass().getMethod(ignoreIf);
        method.setAccessible(true);
        boolean result = (Boolean) method.invoke(object);

        if (hasNotOperator)
            result = !result;

        return result;
    }

    private void sortProperties(List<AccessibleObject> properties) throws DataTypeSerializerException {
        boolean hasorder = false;
        for (AccessibleObject propOn : properties) {
            if (getPropertyAnnotation(propOn).order() != -1)
                hasorder = true;

        }
        if (!hasorder)
            return;

        Collections.sort(properties, new Comparator<Object>() {
            public int compare(Object obj1, Object obj2) {
                try {
                    int order1 = getPropertyAnnotation(obj1).order();
                    int order2 = getPropertyAnnotation(obj2).order();

                    return order1 < order2 ? -1 : order1 == order2 ? 0 : 1;
                } catch (DataTypeSerializerException e) {
                    return 0;
                }
            }
        });
    }

    private DataTypeProperty getPropertyAnnotation(Object property) throws DataTypeSerializerException {
        if (property instanceof Field)
            return ((Field) property).getAnnotation(DataTypeProperty.class);
        else if (property instanceof Method)
            return ((Method) property).getAnnotation(DataTypeProperty.class);

        throw new DataTypeSerializerException("Could not find annotation for property " + property.toString());
    }
}

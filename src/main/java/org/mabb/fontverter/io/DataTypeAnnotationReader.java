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

import org.mabb.fontverter.FontVerterUtils;

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
            if (isVarLengthType && annotationOn.constLength() < 1)
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

    public int getPropertyArrayLength(DataTypeProperty property, Object object)
            throws NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        Field field = FontVerterUtils.findPrivateField(property.arrayLength(), object.getClass());
        if (field != null)
            return field.getInt(object);
        Method method = FontVerterUtils.findPrivateMethod(property.arrayLength(), object.getClass());

        return ((Number) method.invoke(object)).intValue();
    }

    private boolean runIgnoreFilter(Object object, String ignoreIf) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (ignoreIf.isEmpty())
            return false;

        boolean hasNotOperator = false;
        if (ignoreIf.startsWith("!")) {
            hasNotOperator = true;
            ignoreIf = ignoreIf.replace("!", "");
        }

        boolean filterResult = false;
        List<Boolean> originalAccessibility = setAllPropertiesAccessible(object.getClass());

        Object fieldResult = tryGetFieldValue(ignoreIf, object);

        if (fieldResult == null) {
            Method method = object.getClass().getMethod(ignoreIf.replace("()",""));
            filterResult = (Boolean) method.invoke(object);
        }

        if (hasNotOperator)
            filterResult = !filterResult;

        resetAccessibility(originalAccessibility, object.getClass());
        return filterResult;
    }

    private Object tryGetFieldValue(String fieldName, Object object) throws IllegalAccessException {
        try {
            Field field = object.getClass().getField(fieldName);
            return field.get(object);
        } catch (NoSuchFieldException e) {
            return null;
        }
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

    private static List<Boolean> setAllPropertiesAccessible(Class type) {
        List<Boolean> originalAccessiblity = new LinkedList<Boolean>();

        for (Field fieldOn : type.getDeclaredFields()) {
            originalAccessiblity.add(fieldOn.isAccessible());
            fieldOn.setAccessible(true);
        }
        for (Method methodOn : type.getDeclaredMethods()) {
            originalAccessiblity.add(methodOn.isAccessible());
            methodOn.setAccessible(true);
        }

        return originalAccessiblity;
    }

    private static void resetAccessibility(List<Boolean> originalAccessibility, Class type) {
        int i = 0;

        for (Field fieldOn : type.getDeclaredFields()) {
            fieldOn.setAccessible(originalAccessibility.get(i));
            i++;
        }

        for (Method methodOn : type.getDeclaredMethods()) {
            methodOn.setAccessible(originalAccessibility.get(i));
            i++;
        }
    }
}

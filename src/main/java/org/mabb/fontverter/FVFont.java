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

package org.mabb.fontverter;

import org.mabb.fontverter.validator.RuleValidator;
import org.mabb.fontverter.validator.RuleValidator.FontValidatorError;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Less of an adapter and more of a base font class. Silly prefixed name to avoid confusion with jdk Font
 */
public interface FVFont {
    /**
     * @return generated font data
     */
    byte[] getData() throws IOException;

    /**
     * @param fontFile input font file data
     * @return true if the font file is the same font format as this class
     */
    boolean detectFormat(byte[] fontFile);

    /**
     * @param fontFile reads/parses the input font data into this object.
     * @throws IOException error reading font data
     */
    void read(byte[] fontFile) throws IOException;

    // todo: tear this method out and move converter stuff to seperate converter package so dependencies
    // between font types not all messy like?
    FontConverter createConverterForType(FontVerter.FontFormat fontFormat) throws FontNotSupportedException;

    /**
     * @return font name
     */
    String getName();

    /**
     * @return True if strict validation passes.
     */
    boolean isValid();

    /**
     * @return runs strict validator and returns any validation errors with the font.
     */
    List<FontValidatorError> getValidationErrors();

    /**
     * Fixes any validation issues with the font.
     * @throws IOException error reading font data
     */
    void normalize() throws IOException;

    /**
     * @return extra information about the font such as the file ending it should use.
     */
    FontProperties getProperties();
}

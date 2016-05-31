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
     * @throws IOException
     */
    void read(byte[] fontFile) throws IOException;

    // todo: tear this method out and move converter stuff to seperate converter package so dependencies
    // between font types not all messy like?
    FontConverter createConverterForType(FontVerter.FontFormat fontFormat) throws FontNotSupportedException;

    /**
     * @return font name
     */
    String getFontName();

    /**
     * @return True if strict validation passes.
     */
    boolean doesPassStrictValidation();

    /**
     * @return runs strict validator and returns any validation errors with the font.
     */
    List<FontValidatorError> getStrictValidationErrors();

    /**
     * Fixes any validation issues with the font.
     */
    void normalize();

    /**
     * @return extra information about the font such as the file ending it should use.
     */
    FontProperties getProperties();
}
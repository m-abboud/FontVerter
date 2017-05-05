/*
 * Copyright (C) Maddie Abboud 2016
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

package org.mabb.fontverter.eot;

import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.FontNotSupportedException;
import org.mabb.fontverter.FontProperties;
import org.mabb.fontverter.FontVerter;
import org.mabb.fontverter.converter.FontConverter;
import org.mabb.fontverter.io.DataTypeBindingDeserializer;
import org.mabb.fontverter.io.FontDataInputStream;
import org.mabb.fontverter.io.LittleEndianInputStream;
import org.mabb.fontverter.validator.RuleValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EotFont implements FVFont {
    private EotHeader header;

    public byte[] getData() throws IOException {
        return new byte[0];
    }

    public boolean detectFormat(byte[] fontFile) {
        return false;
    }

    public void read(byte[] fontFile) throws IOException {
        LittleEndianInputStream data = new LittleEndianInputStream(fontFile);
        DataTypeBindingDeserializer deserializer = new DataTypeBindingDeserializer();
        header = (EotHeader) deserializer.deserialize(data, EotHeader.class);

    }

    public FontConverter createConverterForType(FontVerter.FontFormat fontFormat) throws FontNotSupportedException {
        return null;
    }

    public String getName() {
        return null;
    }

    public boolean isValid() {
        return true;
    }

    public List<RuleValidator.FontValidatorError> getValidationErrors() {
        return new ArrayList<RuleValidator.FontValidatorError>();
    }

    public void normalize() throws IOException {

    }

    public FontProperties getProperties() {
        return null;
    }

    public EotHeader getHeader() {
        return header;
    }
}

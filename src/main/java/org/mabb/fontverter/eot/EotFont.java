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
import org.mabb.fontverter.converter.EotToOpenTypeConverter;
import org.mabb.fontverter.converter.FontConverter;
import org.mabb.fontverter.converter.IdentityConverter;
import org.mabb.fontverter.io.DataTypeBindingDeserializer;
import org.mabb.fontverter.io.DataTypeBindingSerializer;
import org.mabb.fontverter.io.LittleEndianInputStream;
import org.mabb.fontverter.opentype.OpenTypeFont;
import org.mabb.fontverter.validator.RuleValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EotFont implements FVFont {
    private EotHeader header;
    private OpenTypeFont font;

    public byte[] getData() throws IOException {
        DataTypeBindingSerializer serializer = new DataTypeBindingSerializer();
        return serializer.serialize(header);
    }

    public boolean detectFormat(byte[] fontFile) {
        return false;
    }

    public void read(byte[] fontFile) throws IOException {
        LittleEndianInputStream data = new LittleEndianInputStream(fontFile);
        DataTypeBindingDeserializer deserializer = new DataTypeBindingDeserializer();
        header = (EotHeader) deserializer.deserialize(data, EotHeader.class);

        byte[] fontData = data.readBytes((int) header.fontDataSize);
        font = (OpenTypeFont) FontVerter.readFont(fontData);
    }

    public FontConverter createConverterForType(FontVerter.FontFormat fontFormat) throws FontNotSupportedException {
        if (fontFormat == FontVerter.FontFormat.OTF)
            return new EotToOpenTypeConverter();
        if (fontFormat == FontVerter.FontFormat.EOT)
            return new IdentityConverter();

        throw new FontNotSupportedException("");
    }

    public String getName() {
        return header.getFullNameString();
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
        FontProperties properties = new FontProperties();
        properties.setCssFontFaceFormat("embedded-opentype");
        properties.setFileEnding("EOT");
        properties.setMimeType("application/vnd.ms-fontobject");
        return properties;
    }

    public EotHeader getHeader() {
        return header;
    }

    public OpenTypeFont getEmbeddedFont() {
        return font;
    }

    public void setFont(OpenTypeFont font) {
        this.font = font;
    }
}

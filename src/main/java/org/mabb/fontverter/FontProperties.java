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

package org.mabb.fontverter;

import static org.mabb.fontverter.FontVerterUtils.*;

public class FontProperties {
    private String fileEnding;
    private String mimeType;
    private String cssFontFaceFormat;
    private String name;
    private String fullName;
    private String version;
    private String trademarkNotice;
    private String subFamilyName;
    private String family;

    public String getFileEnding() {
        return fileEnding;
    }

    public void setFileEnding(String fileEnding) {
        this.fileEnding = fileEnding;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getCssFontFaceFormat() {
        return cssFontFaceFormat;
    }

    public void setCssFontFaceFormat(String cssFontFaceFormat) {
        this.cssFontFaceFormat = cssFontFaceFormat;
    }

    public String getName() {
        return nonNullString(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return nonNullString(fullName);
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getVersion() {
        return nonNullString(version);
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTrademarkNotice() {
        return nonNullString(trademarkNotice);
    }

    public void setTrademarkNotice(String trademarkNotice) {
        this.trademarkNotice = trademarkNotice;
    }

    public String getWeight() {
        return nonNullString(subFamilyName);
    }

    public void setSubFamilyName(String subFamilyName) {
        this.subFamilyName = subFamilyName;
    }

    public String getFamily() {
        return nonNullString(family);
    }

    public void setFamily(String family) {
        this.family = family;
    }
}

package org.mabb.fontverter;

public class FontProperties {
    private String fileEnding;
    private String mimeType;

    public String getCssFontFaceFormat() {
        return cssFontFaceFormat;
    }

    public void setCssFontFaceFormat(String cssFontFaceFormat) {
        this.cssFontFaceFormat = cssFontFaceFormat;
    }

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

    private String cssFontFaceFormat;

}

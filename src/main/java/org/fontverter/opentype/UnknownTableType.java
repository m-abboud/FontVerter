package org.fontverter.opentype;

/**
 * Created when parsing un implemented OTF table types for partial font conversion support.
 */
class UnknownTableType extends OpenTypeTable {
    String name = "";

    UnknownTableType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /* big old kludge to handle conversion of tables types that arn't deserializable/parsable yet remove asap*/
    protected boolean isParsingImplemented() {
        return false;
    }
}

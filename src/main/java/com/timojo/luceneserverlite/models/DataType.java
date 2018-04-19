package com.timojo.luceneserverlite.models;

import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

import java.math.BigInteger;

public enum DataType {
    KEYWORD(false, IndexOptions.DOCS, 0), TEXT(true, IndexOptions.DOCS_AND_FREQS_AND_POSITIONS, 0),
    LONG(false, null, Long.BYTES), INTEGER(false, null, Integer.BYTES), BIGINTEGER(false, null, 16),
    SHORT(false, null, Short.BYTES), BYTE(false, null, Byte.BYTES), DOUBLE(false, null, Double.BYTES),
    FLOAT(false, null, Float.BYTES), BOOLEAN(false, IndexOptions.DOCS, 0), DATE(false, null, Long.BYTES),
    UNKNOWN(true, IndexOptions.DOCS_AND_FREQS_AND_POSITIONS, 0);

    int numDimensions = 1;
    int numDimensionBytes = 0;
    boolean tokenized = false;
    IndexOptions indexOptions;

    DataType(boolean tokenized, IndexOptions indexOptions, int numDimensionBytes) {
        this.tokenized = tokenized;
        this.indexOptions = indexOptions;
        this.numDimensionBytes = numDimensionBytes;
    }

    public FieldType getFieldType() {
        FieldType fieldType = new FieldType();
        // TODO - complete
        if (numDimensionBytes > 0) {

        }

        return fieldType;
    }
}

package com.timojo.luceneserverlite.writer;

import io.vertx.core.AbstractVerticle;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;

public class DocumentWriterVerticle extends AbstractVerticle {
    // Wake up every x milliseconds
    // Read the queue
    // for each document in the queue get the index object (is it in memory??)
    // get the indexwriter from the IndexWriterFactory
    // get the field names from the document and lookup the FieldType in the index object
    //          If there is none in the index object use default FieldType
    // if there is an ID field and value use it, otherwise create one
    // create the doc
    // write the doc

    private void playground() {
        Document document = new Document();
        Field field1 = new Field("testField", "testFieldValue", new FieldType());
        document.add(field1);
    }

    // when do we call optimize(numSegments) ??
    // when do we get new readers that can see the stuff that was just written ??
}

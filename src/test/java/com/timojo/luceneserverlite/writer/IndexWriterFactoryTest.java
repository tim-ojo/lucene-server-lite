package com.timojo.luceneserverlite.writer;

import com.timojo.luceneserverlite.models.Index;
import com.timojo.luceneserverlite.models.IndexStatus;
import io.vertx.core.json.JsonObject;
import org.apache.lucene.index.IndexWriter;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class IndexWriterFactoryTest {

    @Test
    public void testGetIndexWriter() {
        // arrange
        JsonObject config = new JsonObject();
        config.put("lucene.index.defaultRAMBufferSizeMB", 2.0);

        IndexWriterFactory.initialize(config);

        Index testIndex = Index.newBuilder()
                .setIndexName("unit_test")
                .setIndexStatus(IndexStatus.OK)
                .setStopWords("some,additional,stop,words")
                .setMergePolicy("LOGBYTESIZEMERGEPOLICY")
                .build();

        // act
        IndexWriter indexWriter = null;
        long startTime = System.nanoTime();
        try {
            indexWriter = IndexWriterFactory.INSTANCE.getIndexWriter(testIndex);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        long duration = System.nanoTime() - startTime;

        // assert
        Assert.assertEquals(2.0, indexWriter.getConfig().getRAMBufferSizeMB(), 0.01);
        Assert.assertEquals("StandardAnalyzer", indexWriter.getAnalyzer().getClass().getSimpleName());
        Assert.assertEquals("LogByteSizeMergePolicy", indexWriter.getConfig().getMergePolicy().getClass().getSimpleName());


        // act - The second time, the indexWriter should come from the Map and not have to be reconstructed
        startTime = System.nanoTime();
        try {
            indexWriter = IndexWriterFactory.INSTANCE.getIndexWriter(testIndex);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        long newDuration = System.nanoTime() - startTime;

        Assert.assertTrue(newDuration < (duration / 100));
    }
}

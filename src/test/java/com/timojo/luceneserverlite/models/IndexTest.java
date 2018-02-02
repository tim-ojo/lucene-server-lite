package com.timojo.luceneserverlite.models;

import io.vertx.core.json.Json;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class IndexTest {
    @Test
    public void testHasDifferences(){
        // arrange
        Index index1 = Index.newBuilder()
                .setIndexName("index1")
                .setIndexStatus(IndexStatus.OK)
                .setCacheQueries(true)
                .setCacheTime(10, TimeUnit.MINUTES)
                .build();

        Index index1Dup = Index.newBuilder()
                .setIndexName("index1")
                .setIndexStatus(IndexStatus.OK)
                .setCacheQueries(true)
                .setCacheTime(10, TimeUnit.MINUTES)
                .build();

        Index index1Diff1 = Index.newBuilder()
                .setIndexName("index1")
                .setCacheTime(10, TimeUnit.SECONDS)
                .build();

        Index index1Diff2 = Index.newBuilder()
                .setIndexName("index1")
                .setIndexStatus(IndexStatus.OK)
                .setCacheQueries(true)
                .setCacheTime(10, TimeUnit.MINUTES)
                .addField("DocName", LuceneType.TEXT)
                .build();

        // act & assert
        Assert.assertFalse(index1.hasDifferences(index1Dup));
        Assert.assertTrue(index1.hasDifferences(index1Diff1));
        Assert.assertTrue(index1.hasDifferences(index1Diff2));
    }

    @Test
    public void testMerge() {
        // arrange
        Index index = Index.newBuilder()
                .setIndexName("index1")
                .setIndexStatus(IndexStatus.OK)
                .setCacheQueries(true)
                .setCacheTime(10, TimeUnit.MINUTES)
                .build();

        Index other = Json.decodeValue("{\"indexName\": \"renamed_index1\"}", Index.class);

        // act
        index.merge(other);

        // assert
        Assert.assertEquals("renamed_index1", index.getIndexName());
        Assert.assertEquals(IndexStatus.OK, index.getIndexStatus());
        Assert.assertEquals(true, index.isCacheQueries());
        Assert.assertEquals(10, index.getCacheTime());
        Assert.assertEquals(TimeUnit.MINUTES, index.getCacheTimeUnit());

        // arrange
        Index index1 = Index.newBuilder()
                .setIndexName("index1")
                .setIndexStatus(IndexStatus.OK)
                .setCacheQueries(true)
                .setCacheTime(10, TimeUnit.MINUTES)
                .addField("bookName", LuceneType.TEXT)
                .addField("author", LuceneType.TEXT)
                .build();

        Index other1 = Json.decodeValue("{\"cacheQueries\": \"false\", \"fields\": {\"year\": \"INT\"}}", Index.class);

        // act
        index1.merge(other1);

        // assert
        Assert.assertEquals("index1", index1.getIndexName());
        Assert.assertEquals(IndexStatus.OK, index1.getIndexStatus());
        Assert.assertEquals(false, index1.isCacheQueries());
        Assert.assertEquals(10, index1.getCacheTime());
        Assert.assertEquals(TimeUnit.MINUTES, index1.getCacheTimeUnit());
        Assert.assertEquals(LuceneType.TEXT, index1.getFields().get("bookName"));
        Assert.assertEquals(LuceneType.TEXT, index1.getFields().get("author"));
        Assert.assertEquals(LuceneType.INT, index1.getFields().get("year"));
    }
}


package com.timojo.luceneserverlite.store;

import com.timojo.luceneserverlite.models.Index;
import com.timojo.luceneserverlite.models.IndexStatus;
import com.timojo.luceneserverlite.models.LuceneType;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.*;

import java.util.concurrent.TimeUnit;

public class IndexStoreTest {
    private IndexStore indexStore = IndexStoreFactory.INSTANCE.getIndexStore();
    private final String testIndexName = "_unittest-idx";

    @Test
    public void testIndexStore(){
        // arrange
        Index testIndex = Index.newBuilder()
                .setIndexName(testIndexName)
                .setIndexStatus(IndexStatus.OK)
                .setCacheQueries(true)
                .setCacheTime(10, TimeUnit.MINUTES)
                .setStopWords("some,additional,stop,words")
                .build();

        // act
        indexStore.store(testIndex);
        Index retrieved = indexStore.get(testIndexName);

        // assert
        Assert.assertEquals(retrieved.getIndexName(), testIndexName);
        Assert.assertEquals(retrieved.isCacheQueries(), true);
        Assert.assertEquals(retrieved.getCacheTime(), 10);
        Assert.assertEquals(retrieved.getStopWords(), "some,additional,stop,words");
        Assert.assertNull(retrieved.getStopWordsAdditive());

        // act
        long indexId = retrieved.getIndexId();
        retrieved.setCacheTime(30);

        indexStore.store(retrieved);
        retrieved = indexStore.get(testIndexName);

        // assert
        Assert.assertEquals(retrieved.getIndexName(), testIndexName);
        Assert.assertEquals(retrieved.getCacheTime(), 30);
        Assert.assertEquals(retrieved.getIndexId(), indexId);
    }
}

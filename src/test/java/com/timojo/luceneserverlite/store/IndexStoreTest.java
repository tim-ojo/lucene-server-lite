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
                .build();

        // act
        indexStore.store(testIndex);
        Index retrieved = indexStore.get(testIndexName);

        // assert
        Assert.assertEquals(retrieved.getIndexName(), testIndexName);
        Assert.assertEquals(retrieved.isCacheQueries(), true);
        Assert.assertEquals(retrieved.getCacheTime(), 10);

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

    @Test
    public void tempTest() {
        String jsonArrStr = "[{\"key1\": \"bob\", \"key2\": \"sue\"},{\"key1\": \"jamaal\", \"key2\":\"shayla\"}]";
        String jsonStr = "{\"key1\": \"bob\", \"key2\": \"sue\"}";

        if (jsonArrStr.startsWith("[") && jsonArrStr.endsWith("]"))
        {
            JsonArray jsonArray = new JsonArray(jsonArrStr);
            jsonArray.forEach(json -> ((JsonObject)json).put("neuKey", "neuValue"));
            System.out.println(jsonArray);
        }

        if (!jsonStr.startsWith("[")) {
            JsonObject jsonObject = new JsonObject("{\"key1\": \"bob\", \"key2\": \"sue\"}");
            System.out.println(jsonObject);
        }
    }
}

package com.timojo.luceneserverlite.store;

import com.timojo.luceneserverlite.exception.ValidationException;
import com.timojo.luceneserverlite.models.Globals;
import com.timojo.luceneserverlite.models.Index;
import com.timojo.luceneserverlite.models.IndexStatus;
import com.timojo.luceneserverlite.util.EventBusAddresses;
import com.timojo.luceneserverlite.util.FileManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class IndexStoreVerticle extends AbstractVerticle {
    private static Logger logger = LoggerFactory.getLogger(IndexStoreVerticle.class);

    private IndexStore indexStore = IndexStoreFactory.INSTANCE.getIndexStore();
    private static final String EMPTY_JSON = "{}";

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(EventBusAddresses.HandleIndexGet.name()).handler(this::handleIndexGetRequest);
        vertx.eventBus().consumer(EventBusAddresses.HandleIndexPut.name()).handler(this::handleIndexPutRequest);
        vertx.eventBus().consumer(EventBusAddresses.HandleIndexDelete.name()).handler(this::handleIndexDeleteRequest);
    }

    private void handleIndexGetRequest(Message<Object> objectMessage) {
        final String indexName = (String) objectMessage.body();

        try {
            if (indexName.equals(Globals.ALL_INDEXES_TOKEN)){
                List<Index> indexList = indexStore.getAll();
                objectMessage.reply(Json.encode(indexList));
            } else {
                Index index = indexStore.get(indexName);
                if (index != null)
                    objectMessage.reply(Json.encode(index));
                else
                    objectMessage.reply(EMPTY_JSON);
            }

        } catch (Exception e) {
            objectMessage.fail(500, e.getMessage());
        }
    }

    private void handleIndexPutRequest(Message<Object> objectMessage) {
        final JsonObject json = (JsonObject) objectMessage.body();
        final String selectedIndex = (String) json.remove(Globals.SEL_INDEX_TOKEN);

        // Kinda hacky but it makes the Json.decodeValue work for the Enum creation (Enum.valueOf())
        upcaseFields(json);

        try {
            Index newIndex = Json.decodeValue(json.toString(), Index.class);

            Index currIndex = selectedIndex != null ? indexStore.get(selectedIndex): indexStore.get(newIndex.getIndexName());
            if (currIndex == null) { // Index doesn't exist already. Create new
                newIndex.validateNew();
                newIndex.setIndexStatus(IndexStatus.OK);

                indexStore.store(newIndex);
                FileManager.createIndexFolder(newIndex.getIndexId());

                objectMessage.reply(Json.encode(newIndex));
            } else { // Modify existing index
                if (currIndex.hasDifferences(newIndex)){
                    newIndex.validate();
                    currIndex.merge(newIndex);
                    indexStore.store(currIndex);

                    objectMessage.reply(Json.encode(currIndex));
                } else {
                    objectMessage.fail(204, "No changes made to index");
                }
            }
        } catch (DecodeException | ValidationException dex) {
            objectMessage.fail(400, dex.getMessage());
            return;
        }
    }

    private void handleIndexDeleteRequest(Message<Object> objectMessage) {
        final String indexName = (String) objectMessage.body();

        try {
            Index index = indexStore.remove(indexName);

            JsonObject jsonObject = new JsonObject();
            if (index != null) {
                FileManager.deleteIndexFolder(index.getIndexId());
                objectMessage.reply(jsonObject.put("msg", "deleted index: " + indexName).toString());
            }
            else {
                objectMessage.reply(jsonObject.put("msg", "index: " + indexName + " not found").toString());
            }

        } catch (Exception e) {
            objectMessage.fail(500, e.getMessage());
        }
    }

    /**
     * Convert all the values in the fields map to uppercase cos the user may have it in lowercase.
     *
     * @param jsonObject
     */
    private void upcaseFields(JsonObject jsonObject) {
        JsonObject fieldsJson = jsonObject.getJsonObject("fields");
        if (fieldsJson != null) {
            for (Map.Entry<String, Object> kvp : fieldsJson.getMap().entrySet())
                fieldsJson.put(kvp.getKey(), ((String)kvp.getValue()).toUpperCase(Locale.ENGLISH));
        }
    }
}

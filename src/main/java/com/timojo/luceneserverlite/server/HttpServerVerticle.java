package com.timojo.luceneserverlite.server;

import com.timojo.luceneserverlite.models.Globals;
import com.timojo.luceneserverlite.store.IndexStore;
import com.timojo.luceneserverlite.store.IndexStoreFactory;
import com.timojo.luceneserverlite.util.EventBusAddresses;
import com.timojo.luceneserverlite.writer.DocumentQueueProvider;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.Iterator;
import java.util.Queue;

/**
 * Main server verticle for handling http request. Registers routes and handlers.
 */
public class HttpServerVerticle extends AbstractVerticle {
    private HttpServer httpServer;
    private IndexStore indexStore = IndexStoreFactory.INSTANCE.getIndexStore();

    private static final String BASE_INDEX_PATH = "index";
    private static final String SEL_INDEX_PATH = "idx";
    private static final String SEL_DOC_PATH = "docId";
    private static final String INDEX_SEARCH_PATH = "_search";

    @Override
    public void start(Future<Void> future) {

        Router router = Router.router(vertx);

        // Bind "/" to our hello message - so we are still compatible.
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end("<h1>Welcome to Lucene Server Lite</h1>");
        });

        // Get all indexes (GET /index/)
        router.get("/" + BASE_INDEX_PATH).handler(this::handleIndexGet);

        // Create an index (PUT|POST /index/{idx-name})
        router.route("/" + BASE_INDEX_PATH + "*").handler(BodyHandler.create());
        router.post("/" + BASE_INDEX_PATH).handler(this::handleIndexPut);
        router.put("/" + BASE_INDEX_PATH+ "/:" + SEL_INDEX_PATH).handler(this::handleIndexPut);

        // Get an index (GET /index/{idx-name})
        router.get("/" + BASE_INDEX_PATH + "/:" + SEL_INDEX_PATH).handler(this::handleIndexGet);

        // Get a doc from an index (GET /{idx-name}/{doc-id})
        router.get("/" + BASE_INDEX_PATH + "/:" + SEL_INDEX_PATH + "/:" + SEL_DOC_PATH);

        // Add a doc to an index (POST /index/{idx-name} {..body..})
        router.post("/" + BASE_INDEX_PATH + "/:" + SEL_INDEX_PATH);

        // Search for a doc in an index (GET /index/{idx-name}/_search?q=)
        router.get("/" + BASE_INDEX_PATH + "/:" + SEL_INDEX_PATH + "/" + INDEX_SEARCH_PATH);

        // Delete an index (GET /index/{idx-name})
        router.delete("/" + BASE_INDEX_PATH + "/:" + SEL_INDEX_PATH).handler(this::handleIndexDelete);

        httpServer = vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(
                    config().getInteger("http.port", 8080),
                    result -> {
                        if (result.succeeded()) {
                            future.complete();
                        } else {
                            future.fail(result.cause());
                        }
                    });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        httpServer.close(stopFuture.completer());
    }

    // Get a document from the index (GET http://host:ip/{idx-name}/{doc-id}):
    //   receive the request, determine the index, check the cache (if configured),
    //      get the SearcherManager, perform the search, release the SearcherManager ASAP, **BLOCKING**
    //   format the results, put into cache (if configured)
    //   send back to client

    // TODO - Figure out how to update a document in an index

    // Read path for a query (GET http://host:ip/index/{idx-name}/_search?q=):
    //   receive the request, determine the index, check the cache (if configured),
    //      get the SearcherManager, perform the search, release the SearcherManager ASAP, **BLOCKING**
    //   format the results, put into cache (if configured)
    //   send back to client


    /**
     * Handles a GET request for a specific index or for all indices. Sends response back to client
     *
     * @param routingContext
     */
    private void handleIndexGet(RoutingContext routingContext) {
        String indexName = Globals.ALL_INDEXES_TOKEN;

        if (routingContext.pathParam(SEL_INDEX_PATH) != null)
            indexName = routingContext.pathParam(SEL_INDEX_PATH);

        vertx.eventBus().send(EventBusAddresses.HandleIndexGet.name(), indexName, new ResponseHandler(routingContext));
    }

    /**
     * Handles PUT and POST requests for creating and updating indices. Returns created/updated index
     *
     * @param routingContext
     */
    private void handleIndexPut(RoutingContext routingContext) {
        String indexName = routingContext.pathParam(SEL_INDEX_PATH);

        try {
            final JsonObject bodyAsJson = routingContext.getBodyAsJson();
            bodyAsJson.put(Globals.SEL_INDEX_TOKEN, indexName);

            vertx.eventBus().send(EventBusAddresses.HandleIndexPut.name(), bodyAsJson, new ResponseHandler(routingContext));
        } catch (DecodeException de) {
            routingContext.response()
                    .setStatusCode(400)
                    .end(de.getMessage());
            return;
        }
    }

    /**
     * Handles POST requests for indexing documents. Returns success message
     *
     * @param routingContext
     */
    private void handleDocPost(RoutingContext routingContext) {
        String indexName = routingContext.pathParam(SEL_INDEX_PATH);

        try {
            if (!indexStore.contains(indexName)) {
                routingContext.response()
                        .setStatusCode(404)
                        .end("Index [" + indexName + "] not found on server. Please first create index before adding documents");
                return;
            }

            String bodyString = routingContext.getBodyAsString();

            JsonArray jsonArray;
            if (bodyString.startsWith("[") && bodyString.endsWith("]")) {
                jsonArray = new JsonArray(bodyString);
                jsonArray.forEach(json -> ((JsonObject)json).put(Globals.SEL_INDEX_TOKEN, indexName));
            } else {
                JsonObject jsonObject = new JsonObject(bodyString);
                jsonObject.put(Globals.SEL_INDEX_TOKEN, indexName);
                jsonArray = new JsonArray();
                jsonArray.add(jsonObject);
            }

            Queue<JsonObject> documentQueue = DocumentQueueProvider.getQueue();

            String resourceExhaustedMessage = "Resource Exhausted: Server document write queue full. Queue Capacity = " +
                    DocumentQueueProvider.getMaxQueueSize();
            if (jsonArray.size() + documentQueue.size() > DocumentQueueProvider.getMaxQueueSize()){
                routingContext.response()
                        .setStatusCode(500)
                        .end(resourceExhaustedMessage);
                return;
            }

            Iterator<Object> iterator = jsonArray.iterator();
            while (iterator.hasNext()) {
                boolean success = documentQueue.offer((JsonObject) iterator.next());
                if (!success) {
                    routingContext.response()
                            .setStatusCode(500)
                            .end(resourceExhaustedMessage);
                    return;
                }
            }

            // TODO
            //   After an amount of time (every 10 seconds), the records in the queue are written to the index and
            //   the searcher is refreshed if necessary
        } catch (DecodeException de) {
            routingContext.response()
                    .setStatusCode(400)
                    .end(de.getMessage());
        } catch (Exception ex) {
            routingContext.response()
                    .setStatusCode(500)
                    .end(ex.getMessage());
        }
    }

    /**
     * Handles a DELETE request for a specific index. Sends response back to client
     *
     * @param routingContext
     */
    private void handleIndexDelete(RoutingContext routingContext) {
        String indexName = routingContext.pathParam(SEL_INDEX_PATH);

        vertx.eventBus().send(EventBusAddresses.HandleIndexDelete.name(), indexName, new ResponseHandler(routingContext));
    }
}

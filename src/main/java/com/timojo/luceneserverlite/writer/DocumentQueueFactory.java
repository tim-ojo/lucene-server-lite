package com.timojo.luceneserverlite.writer;

import io.vertx.core.json.JsonObject;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class DocumentQueueFactory {
    public static DocumentQueueFactory INSTANCE = new DocumentQueueFactory();

    private Queue<JsonObject> documentQueue;
    private static int maxSize = Integer.MAX_VALUE;

    public static void initialize(JsonObject config) {
        Objects.requireNonNull(config, "config cannot be null");
        maxSize = config.getInteger("writer.queue.size", Integer.MAX_VALUE);

        INSTANCE.instantiateDocumentQueue(maxSize);
    }

    public Queue<JsonObject> getQueue() {
        if (documentQueue == null)
            instantiateDocumentQueue(Integer.MAX_VALUE);

        return documentQueue;
    }

    public int getMaxQueueSize() {
        return maxSize;
    }

    private void instantiateDocumentQueue(int maxSize) {
        this.documentQueue = new LinkedBlockingQueue<>(maxSize);
    }
}

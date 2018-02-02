package com.timojo.luceneserverlite.writer;

import io.vertx.core.json.JsonObject;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class DocumentQueueProvider {
    private static Queue<JsonObject> documentQueue;
    private static int maxSize = Integer.MAX_VALUE;

    public static void initializeQueue(int maxSize) {
        if (maxSize > 1) {
            maxSize = maxSize;
        }

        initialize(maxSize);
    }

    private static void initialize(int maxSize) {
        documentQueue = new LinkedBlockingQueue<>(maxSize);
    }

    public static Queue<JsonObject> getQueue() {
        if (documentQueue == null)
            initialize(Integer.MAX_VALUE);

        return documentQueue;
    }

    public static int getMaxQueueSize() {
        return maxSize;
    }
}

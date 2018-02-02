package com.timojo.luceneserverlite.server;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class ResponseHandler implements Handler<AsyncResult<Message<Object>>> {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

    private RoutingContext routingContext;

    public ResponseHandler(RoutingContext routingContext) {
        this.routingContext = routingContext;
    }

    @Override
    public void handle(AsyncResult<Message<Object>> event) {
        final HttpServerResponse response = routingContext.response();

        if (event.succeeded()) {
            response.setStatusCode(200);
            response.putHeader("content-type", "application/json; charset=utf-8");

            final Message<Object> result = event.result();
            response.end((String) result.body());
        } else {
            final ReplyException replyException = (ReplyException) event.cause();
            response.setStatusCode(replyException.failureCode() > 0 ? replyException.failureCode() : 500)
                    .end(replyException.getMessage() == null ? event.toString() : replyException.getMessage());

            logger.error("Ran into an exception: ", replyException);
        }
    }
}

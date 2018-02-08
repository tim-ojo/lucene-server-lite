package com.timojo.luceneserverlite.server;

import com.timojo.luceneserverlite.models.Index;
import com.timojo.luceneserverlite.server.launcher.LauncherVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(VertxUnitRunner.class)
public class HttpServerVerticleTest {
    private Vertx vertx;
    int port = 8081;

    @Before
    public void setUp(TestContext context) {
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", port)
                );

        vertx = Vertx.vertx();
        vertx.deployVerticle(LauncherVerticle.class.getName(), options,
                context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testSampleServer(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(port, "localhost", "/",
                response -> {
                    response.handler(body -> {
                        context.assertTrue(body.toString().contains("Lucene Server Lite"));
                        async.complete();
                    });
                });
    }

    @Test
    public void testIndexPutAndGet(TestContext context) {
        final Async async = context.async();
        final String json = Json.encodePrettily(Index.newBuilder()
                .setIndexName("_inttest-idx")
                .setCacheQueries(true)
                .setCacheTime(1, TimeUnit.HOURS)
                .addField("bookTitle", "text")
                .addField("bookYear", "INT")
                .addField("isbn", "String")
                .setAnalyzer("org.apache.lucene.analysis.de.GermanAnalyzer")
                .build());
        final String length = Integer.toString(json.length());

        vertx.createHttpClient().post(port, "localhost", "/index/")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler(response -> {
                    context.assertEquals(response.statusCode(), 200);
                    context.assertTrue(response.headers().get("content-type").contains("application/json"));
                    response.bodyHandler(body -> {
                        final Index index = Json.decodeValue(body.toString(), Index.class);

                        context.assertEquals("_inttest-idx", index.getIndexName());
                        context.assertEquals(1L, index.getCacheTime());
                        context.assertEquals(3, index.getFields().size());
                        context.assertNotNull(index.getIndexId());

                        async.complete();
                    });
                })
                .write(json)
                .end();

        async.await();
        final Async async2 = context.async();
        vertx.createHttpClient().get(port, "localhost", "/index/_inttest-idx").handler(response -> {
            context.assertEquals(response.statusCode(), 200);
            context.assertTrue(response.headers().get("content-type").contains("application/json"));

            response.bodyHandler(body -> {
                final Index index = Json.decodeValue(body.toString(), Index.class);

                context.assertEquals("_inttest-idx", index.getIndexName());
                context.assertEquals(1L, index.getCacheTime());
                context.assertEquals(3, index.getFields().size());
                context.assertNotNull(index.getIndexId());
                context.assertNull(index.getRamBufferSizeMB());

                async2.complete();
            });
        })
        .end();

        async2.await();
        final Async async3 = context.async();
        vertx.createHttpClient().delete(port, "localhost", "/index/_inttest-idx").handler(response -> {
            context.assertEquals(response.statusCode(), 200);
            context.assertTrue(response.headers().get("content-type").contains("application/json"));

            response.bodyHandler(body -> {
                final JsonObject jsonObject = new JsonObject(body.toString());

                context.assertEquals("deleted index: _inttest-idx", jsonObject.getString("msg"));
                async3.complete();
            });
        }).end();
    }
}

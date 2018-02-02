package com.timojo.luceneserverlite.server.launcher;

import com.timojo.luceneserverlite.server.HttpServerVerticle;
import com.timojo.luceneserverlite.store.IndexStoreVerticle;
import com.timojo.luceneserverlite.util.FileManager;
import com.timojo.luceneserverlite.writer.DocumentQueueProvider;
import com.timojo.luceneserverlite.writer.DocumentWriterVerticle;
import io.vertx.core.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LauncherVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        // initialize environment
        FileManager.ensureDataFolderExists();
        DocumentQueueProvider.initializeQueue(config().getInteger("writer.queue.size", 0));

        // list of all the verticles to deploy
        List<VerticleDeployment> verticleDeployments = Arrays.asList(
                new VerticleDeployment(HttpServerVerticle.class, 1),
                new VerticleDeployment(IndexStoreVerticle.class, 1),
                new VerticleDeployment(DocumentWriterVerticle.class, 1, true)
        );

        // deploy all verticles
        List<Future> allFutures = new ArrayList<>();
        for (VerticleDeployment verticleDeployment : verticleDeployments) {
            deployVerticle(allFutures, verticleDeployment);
        }

        // once all verticles are deployed, register the system as fully started
        CompositeFuture.all(allFutures).setHandler(result -> {
            if (result.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(result.cause());
            }
        });
    }

    private void deployVerticle(Class<? extends Verticle> verticleClass, DeploymentOptions deployOptions, Future<String> future) {
        vertx.deployVerticle(verticleClass.getName(), deployOptions, future.completer());
    }

    private void deployVerticle(List<Future> allVerticleFutures, VerticleDeployment verticleDeployment) {
        final Future<String> deployVerticleFuture = Future.future();
        allVerticleFutures.add(deployVerticleFuture);
        vertx.deployVerticle(verticleDeployment.getVerticleClass().getName(), verticleDeployment.getDeploymentOptions().setConfig(config()), deployVerticleFuture.completer());
    }

    private static class VerticleDeployment {
        private final Class<? extends Verticle> verticleClass;
        private final DeploymentOptions options = new DeploymentOptions();

        VerticleDeployment(Class<? extends Verticle> verticleClass, int instances) {
            this(verticleClass, instances, false);
        }

        VerticleDeployment(Class<? extends Verticle> verticleClass, int instances, boolean workerVerticle) {
            this.verticleClass = verticleClass;

            options.setInstances(instances);
            options.setWorker(workerVerticle);
        }

        Class<? extends Verticle> getVerticleClass() {
            return verticleClass;
        }

        DeploymentOptions getDeploymentOptions() {
            return options;
        }
    }
}
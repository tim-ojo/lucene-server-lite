package com.timojo.luceneserverlite.server.launcher;

import io.vertx.core.Launcher;

/**
 * Launcher class that initializes the Lucene Server Lite application with the necessary configuration options
 *
 * @author timojo
 */
public class ApplicationLauncher extends Launcher {
    public static void main(String[] args) {
        new ApplicationLauncher().dispatch(args);
    }
}

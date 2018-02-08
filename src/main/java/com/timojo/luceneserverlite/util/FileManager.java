package com.timojo.luceneserverlite.util;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for managing index files
 */
public class FileManager {
    public static final Logger logger = LoggerFactory.getLogger(FileManager.class);

    public static void createIndexFolder(long indexId) {
        ensureDataFolderExists();
        createFolderIfNotExist("data" + File.separator + indexId);
    }

    public static void ensureDataFolderExists() {
        createFolderIfNotExist("data");
    }

    private static boolean createFolderIfNotExist(String path) {
        File file = new File(path);

        if (!file.exists())
            return file.mkdir();
        else
            return false;
    }

    public static void deleteIndexFolder(long indexId) {
        try {
            Files.delete(Paths.get("data" + File.separator + indexId));
        } catch (IOException e) {
            logger.error("Could not delete index folder: " + indexId, e);
        }
    }

    public static Path indexPath(long indexId) {
        return Paths.get("data" + File.separator + indexId);
    }
}

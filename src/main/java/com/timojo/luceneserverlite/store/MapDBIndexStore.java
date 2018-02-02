package com.timojo.luceneserverlite.store;

import com.timojo.luceneserverlite.models.Index;
import com.timojo.luceneserverlite.util.FileManager;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * Index Store implementation backed by MapDB for a persistent store
 */
public class MapDBIndexStore implements IndexStore {
    private static final String INDEX_DB_FILE = "data"+ File.separator +"indexes.db";
    private DB db;
    private ConcurrentMap map;

    // TODO: Take in any configs here
    public MapDBIndexStore() {
        FileManager.ensureDataFolderExists();

        db = DBMaker.fileDB(INDEX_DB_FILE)
                .fileMmapEnable() // Use memory-mapped files even on 32-bit systems
                .closeOnJvmShutdown()
                .make();
        db.getStore().fileLoad();

        map = db.hashMap("indexes").createOrOpen();
    }

    @Override
    public Index get(String indexName) {
        if (indexName == null || !map.containsKey(indexName))
            return null;

        return (Index)map.get(indexName);
    }

    @Override
    public List<Index> getAll() {
        List<Index> all = new ArrayList<>(map.size());
        map.values().forEach((val) -> all.add((Index) val));
        return all;
    }

    @Override
    public void store(Index index) {
        map.put(index.getIndexName(), index);
        db.commit();
    }

    public boolean contains(String indexName) {
        return map.containsKey(indexName);
    }

    @Override
    public Index remove(String indexName) {
        return (Index) map.remove(indexName);
    }

    @Override
    public void close() {
        if (db != null && !db.isClosed())
            db.close();
    }
}

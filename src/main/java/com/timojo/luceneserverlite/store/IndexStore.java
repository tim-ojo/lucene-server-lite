package com.timojo.luceneserverlite.store;

import com.timojo.luceneserverlite.models.Index;

import java.util.List;

/**
 * A repository for storing and retrieving index metadata
 */
public interface IndexStore {
    /**
     * Get the named index object from the store, if it exists. Returns null if not found
     *
     * @return
     */
    Index get(String indexName);

    /**
     * Returns true if the store contains the specified index, false if not
     *
     * @return
     */
    boolean contains(String indexName);

    /**
     * Get all index objects from the store.
     *
     * Note: There is no paging applied so the size of the returns must fit in memory
     *
     * @return
     */
    List<Index> getAll();

    /**
     * Persist an index object to the store
     *
     * @param index
     */
    void store(Index index);

    /**
     * Removes the named index object from the store, if it exists. Returns null if not found
     *
     * @return
     */
    Index remove(String indexName);

    /**
     * Close the store and cleanup any resources
     */
    void close();
}

package com.timojo.luceneserverlite.store;

public enum IndexStoreFactory {
    INSTANCE;

    IndexStoreFactory() {
        indexStore = new MapDBIndexStore();
    }

    private IndexStore indexStore;

    public IndexStore getIndexStore() {
        return indexStore;
    }
}

package com.timojo.luceneserverlite.models;

import com.timojo.luceneserverlite.exception.ValidationException;
import com.timojo.luceneserverlite.util.InputChecker;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Index implements Serializable {
    private long indexId;
    private String indexName;
    private IndexStatus indexStatus = IndexStatus.UNKNOWN;
    private long documentCount;
    private Boolean cacheQueries;
    private Long cacheTime;
    private TimeUnit cacheTimeUnit;
    private Map<String, LuceneType> fields = new HashMap<>();

    private Index() {
        this.indexId = System.currentTimeMillis();
    }

    private Index(Builder builder) {
        InputChecker.isNotNullOrEmpty(builder.indexName, "indexName must be set");

        this.indexId = System.currentTimeMillis();
        this.indexName = builder.indexName;
        this.indexStatus = builder.indexStatus;
        this.cacheQueries = builder.cacheQueries;
        this.cacheTime = builder.cacheTime;
        this.cacheTimeUnit = builder.cacheTimeUnit;
        this.fields = builder.fields;
    }

    public long getIndexId() {
        return indexId;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public IndexStatus getIndexStatus() {
        return indexStatus;
    }

    public void setIndexStatus(IndexStatus status) {
        this.indexStatus = status;
    }

    public long getDocumentCount() {
        return documentCount;
    }

    public boolean isCacheQueries() {
        return cacheQueries != null && cacheQueries.booleanValue();
    }

    public void setCacheQueries(boolean cacheQueries) {
        this.cacheQueries = cacheQueries;
    }

    public long getCacheTime() {
        return cacheTime == null ? 0L : cacheTime;
    }

    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    public TimeUnit getCacheTimeUnit() {
        return cacheTimeUnit;
    }

    public void setCacheTimeUnit(TimeUnit cacheTimeUnit) {
        this.cacheTimeUnit = cacheTimeUnit;
    }

    public Map<String, LuceneType> getFields() {
        return fields;
    }

    public void setFields(Map<String, LuceneType> fields) {
        this.fields = fields;
    }

    public boolean hasDifferences(Index other) {
        if (other == null)
            throw new IllegalArgumentException("Passed in object for comparison is null");

        if (other.indexName != null && !other.indexName.equals("") && !other.indexName.equals(this.indexName)) return true;
        if (other.indexStatus != null && other.indexStatus != this.indexStatus) return true;
        if (other.cacheQueries != null && other.cacheQueries != this.cacheQueries) return true;
        if (other.cacheTime != null && other.cacheTime != this.cacheTime) return true;
        if (other.cacheTimeUnit != null && other.cacheTimeUnit != this.cacheTimeUnit) return true;
        if (other.fields != null && !other.fields.isEmpty() && !other.fields.equals(this.fields)) return true;

        return false;
    }

    public void merge(Index other) {
        if (other == null)
            throw new IllegalArgumentException("Passed in object for merging is null");

        if (other.indexName != null && !other.indexName.equals("")) this.indexName = other.indexName;
        if (other.indexStatus != null && other.indexStatus != IndexStatus.UNKNOWN) this.indexStatus = other.indexStatus;
        if (other.cacheQueries != null && other.cacheQueries != this.cacheQueries) this.cacheQueries = other.cacheQueries;
        if (other.cacheTime != null && other.cacheTime.equals(this.cacheTime)) this.cacheTime = other.cacheTime;
        if (other.cacheTimeUnit != null && other.cacheTimeUnit != this.cacheTimeUnit) this.cacheTimeUnit = other.cacheTimeUnit;
        if (other.fields != null) this.fields.putAll(other.fields);
    }

    public void validate() throws ValidationException {
        if (this.indexName == null || this.indexName.length() == 0)
            throw new ValidationException("indexName was not provided for new index");

        if (this.fields == null || this.fields.size() == 0)
            throw new ValidationException("At least one field must be provided for new index");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Index index = (Index) o;

        if (indexName != null ? !indexName.equals(index.indexName) : index.indexName != null) return false;
        if (indexStatus != index.indexStatus) return false;
        if (cacheQueries != null ? !cacheQueries.equals(index.cacheQueries) : index.cacheQueries != null) return false;
        if (cacheTime != null ? !cacheTime.equals(index.cacheTime) : index.cacheTime != null) return false;
        if (cacheTimeUnit != index.cacheTimeUnit) return false;
        return fields != null ? fields.equals(index.fields) : index.fields == null;
    }

    @Override
    public int hashCode() {
        int result = indexName != null ? indexName.hashCode() : 0;
        result = 31 * result + (indexStatus != null ? indexStatus.hashCode() : 0);
        result = 31 * result + (cacheQueries != null ? cacheQueries.hashCode() : 0);
        result = 31 * result + (cacheTime != null ? cacheTime.hashCode() : 0);
        result = 31 * result + (cacheTimeUnit != null ? cacheTimeUnit.hashCode() : 0);
        result = 31 * result + (fields != null ? fields.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Index{" +
                "indexId=" + indexId +
                ", indexName='" + indexName + '\'' +
                ", indexStatus=" + indexStatus +
                ", documentCount=" + documentCount +
                ", cacheQueries=" + cacheQueries +
                ", cacheTime=" + cacheTime +
                ", cacheTimeUnit=" + cacheTimeUnit +
                ", fields=" + fields +
                '}';
    }

    public static Builder newBuilder(){
        return new Builder();
    }

    public static class Builder {
        private String indexName;
        private IndexStatus indexStatus = IndexStatus.UNKNOWN;
        private boolean cacheQueries = false;
        private long cacheTime = 0L;
        private TimeUnit cacheTimeUnit = TimeUnit.MILLISECONDS;
        private Map<String, LuceneType> fields = new HashMap<>();

        public Builder setIndexName(String indexName) {
            this.indexName = indexName;
            return this;
        }

        public Builder setIndexStatus(IndexStatus indexStatus) {
            this.indexStatus = indexStatus;
            return this;
        }

        public Builder setCacheQueries(boolean cacheQueries) {
            this.cacheQueries = cacheQueries;
            return this;
        }

        public Builder setCacheTime(long cacheTime, TimeUnit cacheTimeUnit) {
            this.cacheTime = cacheTime;
            this.cacheTimeUnit = cacheTimeUnit;
            return this;
        }

        public Builder addAllFields(Map<String, LuceneType> fields) {
            if (fields != null)
                this.fields.putAll(fields);
            return this;
        }

        public Builder addField(String fieldName, String fieldType) {
            this.fields.put(fieldName, LuceneType.valueOf(fieldType.toUpperCase(Locale.ENGLISH)));
            return this;
        }

        public Builder addField(String fieldName, LuceneType fieldType) {
            this.fields.put(fieldName, fieldType);
            return this;
        }

        public Builder setFields(Map<String, LuceneType> fields) {
            if (fields != null)
                this.fields = fields;
            return this;
        }

        public Index build() {
            return new Index(this);
        }
    }
}

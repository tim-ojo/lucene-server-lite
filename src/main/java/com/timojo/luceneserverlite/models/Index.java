package com.timojo.luceneserverlite.models;

import com.timojo.luceneserverlite.exception.UnknownAnalyzerException;
import com.timojo.luceneserverlite.exception.UnknownMergePolicyException;
import com.timojo.luceneserverlite.exception.ValidationException;
import com.timojo.luceneserverlite.util.AnalyzerHelper;
import com.timojo.luceneserverlite.util.InputChecker;
import com.timojo.luceneserverlite.util.MergePolicyHelper;
import org.apache.lucene.analysis.Analyzer;

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

    private Boolean useCompoundFile;
    private Double ramBufferSizeMB;
    private String stopWords;
    private Boolean stopWordsAdditive;
    private String analyzer;
    private String mergePolicy;

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

        this.useCompoundFile = builder.useCompoundFile;
        this.ramBufferSizeMB = builder.ramBufferSizeMB;
        this.stopWords = builder.stopWords;
        this.stopWordsAdditive = builder.stopWordsAdditive;
        this.analyzer = builder.analyzer;
        this.mergePolicy = builder.mergePolicy;
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

    public Boolean getCacheQueries() {
        return cacheQueries;
    }

    public Boolean getUseCompoundFile() {
        return useCompoundFile;
    }

    public Double getRamBufferSizeMB() {
        return ramBufferSizeMB;
    }

    public String getStopWords() {
        return stopWords;
    }

    public Boolean getStopWordsAdditive() {
        return stopWordsAdditive;
    }

    public String getAnalyzer() {
        return analyzer;
    }

    public String getMergePolicy() {
        return mergePolicy;
    }

    public boolean hasDifferences(Index other) {
        if (other == null)
            throw new IllegalArgumentException("Passed in object for comparison is null");

        if (other.indexName != null && !other.indexName.equals("") && !other.indexName.equals(this.indexName)) return true;
        if (other.indexStatus != null && other.indexStatus != this.indexStatus) return true;
        if (other.cacheQueries != null && !other.cacheQueries.equals(this.cacheQueries)) return true;
        if (other.cacheTime != null && !other.cacheTime.equals(this.cacheTime)) return true;
        if (other.cacheTimeUnit != null && other.cacheTimeUnit != this.cacheTimeUnit) return true;
        if (other.fields != null && !other.fields.isEmpty() && !other.fields.equals(this.fields)) return true;

        if (other.useCompoundFile != this.useCompoundFile) return true;
        if (other.ramBufferSizeMB != null && !other.ramBufferSizeMB.equals(this.ramBufferSizeMB)) return true;
        if (other.stopWords != null && !other.stopWords.equals("") && !other.stopWords.equals(this.stopWords)) return true;
        if (other.stopWordsAdditive != this.stopWordsAdditive) return true;
        if (other.analyzer != null && !other.analyzer.equals("") && !other.analyzer.equals(this.analyzer)) return true;
        if (other.mergePolicy != null && !other.mergePolicy.equals("") && !other.mergePolicy.equals(this.mergePolicy)) return true;

        return false;
    }

    public void merge(Index other) {
        if (other == null)
            throw new IllegalArgumentException("Passed in object for merging is null");

        if (other.indexName != null && !other.indexName.equals("")) this.indexName = other.indexName;
        if (other.indexStatus != null && other.indexStatus != IndexStatus.UNKNOWN) this.indexStatus = other.indexStatus;
        if (other.cacheQueries != null && other.cacheQueries != this.cacheQueries) this.cacheQueries = other.cacheQueries;
        if (other.cacheTime != null && !other.cacheTime.equals(this.cacheTime)) this.cacheTime = other.cacheTime;
        if (other.cacheTimeUnit != null && other.cacheTimeUnit != this.cacheTimeUnit) this.cacheTimeUnit = other.cacheTimeUnit;
        if (other.fields != null) this.fields.putAll(other.fields);

        if (other.useCompoundFile != this.useCompoundFile) this.useCompoundFile = other.useCompoundFile;
        if (other.ramBufferSizeMB != null && !other.ramBufferSizeMB.equals(this.ramBufferSizeMB))
            this.ramBufferSizeMB = other.ramBufferSizeMB;
        if (other.stopWords != null && !other.stopWords.equals("") && !other.stopWords.equals(this.stopWords))
            this.stopWords = other.stopWords;
        if (other.stopWordsAdditive != this.stopWordsAdditive) this.stopWordsAdditive = other.stopWordsAdditive;
        if (other.analyzer != null && !other.analyzer.equals("") && !other.analyzer.equals(this.analyzer))
            this.analyzer = other.analyzer;
        if (other.mergePolicy != null && !other.mergePolicy.equals("") && !other.mergePolicy.equals(this.mergePolicy))
            this.mergePolicy = other.mergePolicy;
    }

    public void validateNew() throws ValidationException {
        if (this.indexName == null || this.indexName.length() == 0)
            throw new ValidationException("indexName was not provided for new index");

        if (this.fields == null || this.fields.size() == 0)
            throw new ValidationException("At least one field must be provided for new index");

        validate();
    }

    public void validate() throws ValidationException {
        if (analyzer != null && !analyzer.isEmpty()) {
            try {
                AnalyzerHelper.getAnalyzerObjectByName(analyzer, null);
            } catch (UnknownAnalyzerException uae) {
                throw new ValidationException(uae.getMessage());
            }
        }

        if (mergePolicy != null && !mergePolicy.isEmpty()) {
            try {
                MergePolicyHelper.getMergePolicyObjectByName(mergePolicy);
            } catch (UnknownMergePolicyException umpe) {
                throw new ValidationException(umpe.getMessage());
            }
        }

        if (ramBufferSizeMB != null && ramBufferSizeMB <= 0)
            throw new ValidationException("ramBufferSizeMB must be greater than 0");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Index index = (Index) o;

        if (indexId != index.indexId) return false;
        if (documentCount != index.documentCount) return false;
        if (useCompoundFile != index.useCompoundFile) return false;
        if (Double.compare(index.ramBufferSizeMB, ramBufferSizeMB) != 0) return false;
        if (stopWordsAdditive != index.stopWordsAdditive) return false;
        if (indexName != null ? !indexName.equals(index.indexName) : index.indexName != null) return false;
        if (indexStatus != index.indexStatus) return false;
        if (cacheQueries != null ? !cacheQueries.equals(index.cacheQueries) : index.cacheQueries != null) return false;
        if (cacheTime != null ? !cacheTime.equals(index.cacheTime) : index.cacheTime != null) return false;
        if (cacheTimeUnit != index.cacheTimeUnit) return false;
        if (!fields.equals(index.fields)) return false;
        if (stopWords != null ? !stopWords.equals(index.stopWords) : index.stopWords != null) return false;
        if (analyzer != null ? !analyzer.equals(index.analyzer) : index.analyzer != null) return false;
        return mergePolicy != null ? mergePolicy.equals(index.mergePolicy) : index.mergePolicy == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (indexId ^ (indexId >>> 32));
        result = 31 * result + (indexName != null ? indexName.hashCode() : 0);
        result = 31 * result + (indexStatus != null ? indexStatus.hashCode() : 0);
        result = 31 * result + (int) (documentCount ^ (documentCount >>> 32));
        result = 31 * result + (cacheQueries != null ? cacheQueries.hashCode() : 0);
        result = 31 * result + (cacheTime != null ? cacheTime.hashCode() : 0);
        result = 31 * result + (cacheTimeUnit != null ? cacheTimeUnit.hashCode() : 0);
        result = 31 * result + fields.hashCode();
        result = 31 * result + (useCompoundFile ? 1 : 0);
        temp = Double.doubleToLongBits(ramBufferSizeMB);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (stopWords != null ? stopWords.hashCode() : 0);
        result = 31 * result + (stopWordsAdditive ? 1 : 0);
        result = 31 * result + (analyzer != null ? analyzer.hashCode() : 0);
        result = 31 * result + (mergePolicy != null ? mergePolicy.hashCode() : 0);
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
                ", useCompoundFile=" + useCompoundFile +
                ", ramBufferSizeMB=" + ramBufferSizeMB +
                ", stopWords='" + stopWords + '\'' +
                ", stopWordsAdditive=" + stopWordsAdditive +
                ", analyzer='" + analyzer + '\'' +
                ", mergePolicy='" + mergePolicy + '\'' +
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

        private Boolean useCompoundFile;
        private Double ramBufferSizeMB;
        private String stopWords;
        private Boolean stopWordsAdditive;
        private String analyzer;
        private String mergePolicy;

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

        public Builder setUseCompoundFile(boolean useCompoundFile) {
            this.useCompoundFile = useCompoundFile;
            return this;
        }

        public Builder setRamBufferSizeMB(double ramBufferSizeMB) {
            this.ramBufferSizeMB = ramBufferSizeMB;
            return this;
        }

        public Builder setStopWords(String stopWords) {
            this.stopWords = stopWords;
            return this;
        }

        public Builder setStopWordsAdditive(boolean stopWordsAdditive) {
            this.stopWordsAdditive = stopWordsAdditive;
            return this;
        }

        public Builder setAnalyzer(String analyzer) {
            this.analyzer = analyzer;
            return this;
        }

        public Builder setMergePolicy(String mergePolicy) {
            this.mergePolicy = mergePolicy;
            return this;
        }

        public Index build() {
            return new Index(this);
        }
    }
}

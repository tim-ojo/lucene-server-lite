package com.timojo.luceneserverlite.writer;

import com.timojo.luceneserverlite.exception.UnknownAnalyzerException;
import com.timojo.luceneserverlite.exception.UnknownMergePolicyException;
import com.timojo.luceneserverlite.models.Index;
import com.timojo.luceneserverlite.util.AnalyzerHelper;
import com.timojo.luceneserverlite.util.FileManager;
import com.timojo.luceneserverlite.util.MergePolicyHelper;
import com.timojo.luceneserverlite.util.StopWordHelper;
import io.vertx.core.json.JsonObject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.TieredMergePolicy;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


public class IndexWriterFactory {
    public static IndexWriterFactory INSTANCE = new IndexWriterFactory();

    private static String defMergePolicyStr;
    private static String defAnalyzerStr;
    private static String defStopWordsSetStr;
    private static boolean defStopWordsAdditive;
    private static boolean defUseCompoundFile;
    private static double defRamBufferSize;

    private static boolean _initialized = false;

    private Map<Long, IndexWriter> indexWriterMap = new ConcurrentHashMap<>();

    /**
     * TODO - Documentation
     * Setup the defaults so that if a writer is requested and doesn't specify stuff, we can use the defaults
     * takes in some sort config and initialize the indexWriter
     *
     * @param config
     */
    public static void initialize(JsonObject config) {
        Objects.requireNonNull(config, "config cannot be null");

        defUseCompoundFile = config.getBoolean("lucene.index.defaultUseCompoundFile", true);
        defRamBufferSize = config.getDouble("lucene.index.defaultRAMBufferSizeMB", IndexWriterConfig.DEFAULT_RAM_BUFFER_SIZE_MB);

        defStopWordsSetStr = config.getString("lucene.index.customStopWordsSet");
        defStopWordsAdditive = config.getBoolean("lucene.index.customStopWordsAdditive", true);

        defAnalyzerStr = config.getString("lucene.index.defaultAnalyzer");
        defMergePolicyStr = config.getString("lucene.index.defaultMergePolicy");

        _initialized = true;
    }

    private static CharArraySet getStopWordSet(String stopWordsStr, boolean stopWordsAdditive) {
        return StopWordHelper.getStopWordSet(stopWordsStr, stopWordsAdditive);
    }

    private static Analyzer getAnalyzer(String analyzerStr, CharArraySet stopWordsSet) {
        Analyzer analyzer;
        try {
            analyzer = AnalyzerHelper.getAnalyzerObjectByName(analyzerStr, stopWordsSet);
        } catch (UnknownAnalyzerException ex) {
            analyzer = new StandardAnalyzer();
        }

        return analyzer;
    }

    private static MergePolicy getMergePolicy(String mergePolicyStr) {
        MergePolicy mergePolicy;
        try {
            mergePolicy = MergePolicyHelper.getMergePolicyObjectByName(mergePolicyStr);
        } catch (UnknownMergePolicyException ex) {
            mergePolicy = new TieredMergePolicy();
        }

        return mergePolicy;
    }

    /**
     * TODO - Documentation
     *
     * @param index
     * @return
     */
    public IndexWriter getIndexWriter(Index index) throws IOException {
        if (!_initialized)
            throw new IllegalStateException("IndexWriterFactory has not been properly initialized yet. " +
                    "Ensure `initialize(JsonObject config)` is called at the start of your application");

        if (indexWriterMap.containsKey(index.getIndexId())) {
            return indexWriterMap.get(index.getIndexId());
        }
        else {
            Directory directory = FSDirectory.open(FileManager.indexPath(index.getIndexId()));

            String stopWordsSetStr = (index.getStopWords() != null && !index.getStopWords().isEmpty()) ?
                    index.getStopWords() : defStopWordsSetStr;
            boolean stopWordsAdditive = index.getStopWordsAdditive() != null ? index.getStopWordsAdditive() : defStopWordsAdditive;
            CharArraySet stopWordsSet = getStopWordSet(stopWordsSetStr, stopWordsAdditive);

            String analyzerStr = (index.getAnalyzer() != null && !index.getAnalyzer().isEmpty()) ?
                    index.getAnalyzer() : defAnalyzerStr;
            Analyzer analyzer = getAnalyzer(analyzerStr, stopWordsSet);

            String mergePolicyStr = (index.getMergePolicy() != null && !index.getMergePolicy().isEmpty()) ?
                    index.getMergePolicy() : defMergePolicyStr;
            MergePolicy mergePolicy = getMergePolicy(mergePolicyStr);

            boolean useCompoundFile = (index.getUseCompoundFile() != null) ? index.getUseCompoundFile() : defUseCompoundFile;
            double ramBufferSizeMB = index.getRamBufferSizeMB() != null ? index.getRamBufferSizeMB() : defRamBufferSize;


            IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer)
                    .setMergePolicy(mergePolicy)
                    .setUseCompoundFile(useCompoundFile)
                    .setRAMBufferSizeMB(ramBufferSizeMB));

            indexWriterMap.put(index.getIndexId(), indexWriter);
            return indexWriter;
        }
    }

}

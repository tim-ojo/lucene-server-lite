package com.timojo.luceneserverlite.util;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class StopWordHelper {
    public static CharArraySet getStopWordSet(String stopWordsStr, boolean additive) {
        List<String> stopWordsCollection = stopWordsStr != null
                ? Arrays.asList(stopWordsStr.toLowerCase(Locale.ENGLISH).split(","))
                : Collections.emptyList();
        CharArraySet stopWordsSet = new CharArraySet(stopWordsCollection, true);

        if (additive)
            stopWordsSet.addAll(StandardAnalyzer.STOP_WORDS_SET);

        return stopWordsSet;
    }
}

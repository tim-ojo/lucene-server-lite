package com.timojo.luceneserverlite.util;

import com.timojo.luceneserverlite.exception.UnknownAnalyzerException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.UnicodeWhitespaceAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.lang.reflect.Constructor;
import java.util.Locale;

public class AnalyzerHelper {
    public static Analyzer getAnalyzerObjectByName(String analyzerNameString, CharArraySet stopWordsSet) throws UnknownAnalyzerException {
        String analyzerName = (analyzerNameString == null || analyzerNameString.isEmpty()) ?
                "STANDARD" : analyzerNameString.toUpperCase(Locale.ENGLISH);

        Analyzer analyzer;
        switch (analyzerName) {
            case "STANDARD":
                analyzer = (stopWordsSet == null || stopWordsSet.isEmpty()) ?
                        new StandardAnalyzer() :
                        new StandardAnalyzer(stopWordsSet);
                break;
            case "STOP":
                analyzer = (stopWordsSet == null || stopWordsSet.isEmpty()) ?
                        new StopAnalyzer() :
                        new StopAnalyzer(stopWordsSet);
                break;
            case "KEYWORD":
                analyzer = new KeywordAnalyzer();
                break;
            case "SIMPLE":
                analyzer = new SimpleAnalyzer();
                break;
            case "WHITESPACE":
                analyzer = new WhitespaceAnalyzer();
                break;
            case "UNICODEWHITESPACE":
                analyzer = new UnicodeWhitespaceAnalyzer();
                break;
            default:
                analyzer = instantiateAnalyzer(analyzerNameString, stopWordsSet);
        }

        return analyzer;
    }

    private static Analyzer instantiateAnalyzer(String analyzerName, CharArraySet stopWordsSet) throws UnknownAnalyzerException {
        Analyzer analyzer;

        try {
            Class<?> clazz = Class.forName(analyzerName);
            if (StopwordAnalyzerBase.class.isAssignableFrom(clazz) && stopWordsSet != null && !stopWordsSet.isEmpty()) {
                Constructor<?> constructor = clazz.getConstructor(CharArraySet.class);
                analyzer = (Analyzer) constructor.newInstance(stopWordsSet);
            } else {
                analyzer = (Analyzer) clazz.newInstance();
            }
        } catch (Exception ex) {
            throw new UnknownAnalyzerException("Exception creating analyzer instance for input: " + analyzerName +
                    ". Please provide the full canonical name of a class on the classpath", ex);
        }

        return analyzer;
    }
}

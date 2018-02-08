package com.timojo.luceneserverlite.util;

import com.timojo.luceneserverlite.exception.UnknownMergePolicyException;
import org.apache.lucene.index.LogByteSizeMergePolicy;
import org.apache.lucene.index.LogDocMergePolicy;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.NoMergePolicy;
import org.apache.lucene.index.TieredMergePolicy;

import java.util.Locale;

public class MergePolicyHelper {

    public static MergePolicy getMergePolicyObjectByName(String mergePolicyString) throws UnknownMergePolicyException {
        String policyName = (mergePolicyString == null || mergePolicyString.isEmpty()) ?
                "TIEREDMERGEPOLICY" : mergePolicyString.toUpperCase(Locale.ENGLISH);

        MergePolicy mergePolicy;
        switch (policyName) {
            case "TIEREDMERGEPOLICY":
                mergePolicy = new TieredMergePolicy();
                break;
            case "LOGBYTESIZEMERGEPOLICY":
                mergePolicy = new LogByteSizeMergePolicy();
                break;
            case "LOGDOCMERGEPOLICY":
                mergePolicy = new LogDocMergePolicy();
                break;
            case "NOMERGEPOLICY":
                mergePolicy = NoMergePolicy.INSTANCE;
                break;
            default:
                mergePolicy = instantiateMergePolicy(mergePolicyString);
        }

        return mergePolicy;
    }

    private static MergePolicy instantiateMergePolicy(String mergePolicyName) throws UnknownMergePolicyException {
        MergePolicy mergePolicy;

        try {
            Class<?> clazz = Class.forName(mergePolicyName);
            mergePolicy = (MergePolicy) clazz.newInstance();
        } catch (Exception ex) {
            throw new UnknownMergePolicyException("Exception creating MergePolicy instance for input: " + mergePolicyName
                    + ". Please provide the full canonical name of a class on the classpath", ex);
        }

        return mergePolicy;
    }
}

package com.penghai.customized;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.DefaultSimilarity;

public class CustomSimilarity extends DefaultSimilarity {

    @Override
    public float lengthNorm(FieldInvertState state) {
        // simply return the field's configured boost value
        // instead of also factoring in the field's length
        return state.getBoost();
    }

    @Override
    public float idf(long docFreq, long numDocs) {
        // more-heavily weight terms that appear infrequently
        return (float) (Math.sqrt(numDocs/(double)(docFreq+1)) + 1.0);
    }
}
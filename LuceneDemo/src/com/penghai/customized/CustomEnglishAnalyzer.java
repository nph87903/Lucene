package com.penghai.customized;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

public class CustomEnglishAnalyzer extends StopwordAnalyzerBase {

    /** Tokens longer than this length are discarded. Defaults to 50 chars. */
    public int maxTokenLength = 50;

    public CustomEnglishAnalyzer() {
        super(Version.LUCENE_46, StandardAnalyzer.STOP_WORDS_SET);
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        final StandardTokenizer source = new StandardTokenizer(matchVersion, reader);
        source.setMaxTokenLength(maxTokenLength);

        TokenStream pipeline = source;
        pipeline = new StandardFilter(matchVersion, pipeline);
        pipeline = new EnglishPossessiveFilter(matchVersion, pipeline);
        pipeline = new ASCIIFoldingFilter(pipeline);
        pipeline = new LowerCaseFilter(matchVersion, pipeline);
        pipeline = new StopFilter(matchVersion, pipeline, stopwords);
        pipeline = new PorterStemFilter(pipeline);
        return new TokenStreamComponents(source, pipeline);
    }
}
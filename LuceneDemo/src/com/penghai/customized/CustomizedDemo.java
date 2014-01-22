package com.penghai.customized;

import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class CustomizedDemo {
	
	Query buildQuery(String phrase, ArrayList<String> fields, String defaultField) {
		ArrayList<String> words = tokenizePhrase(phrase);
        BooleanQuery q = new BooleanQuery();

        // create term combinations if there are multiple words in the query
        if (words.size() > 1) {
            // exact-phrase query
            PhraseQuery phraseQ = new PhraseQuery();
            for (int w = 0; w < words.size(); w++)
                phraseQ.add(new Term(defaultField, words.get(w)));
            phraseQ.setBoost(words.size() * 5);
            phraseQ.setSlop(2);
            q.add(phraseQ, BooleanClause.Occur.SHOULD);

            // 2 out of 4, 3 out of 4, 4 out of 4 (any order), etc
            // stop at 7 in case user enters a pathologically long query
            int maxRequired = Math.min(words.size(), 7);
            for (int minRequired = 2; minRequired <= maxRequired; minRequired++) { 
                BooleanQuery comboQ = new BooleanQuery();
                for (int w = 0; w < words.size(); w++){
                	PhraseQuery tempP = new  PhraseQuery();
                	tempP.add(new Term(defaultField, words.get(w)));
                	comboQ.add(tempP, BooleanClause.Occur.SHOULD);
                }
                    comboQ.setBoost(minRequired * 3);
                comboQ.setMinimumNumberShouldMatch(minRequired);
                q.add(comboQ, BooleanClause.Occur.SHOULD);
            }
        }

        // create an individual term query for each word for each field
        for (int w = 0; w < words.size(); w++)
            for (int f = 0; f < fields.size(); f++){
            	PhraseQuery tempP = new  PhraseQuery();
            	tempP.add(new Term(fields.get(f), words.get(w)));
            	q.add(tempP, BooleanClause.Occur.SHOULD);          
            	}
                

        return q;
    }
	
	ArrayList<String> tokenizePhrase(String phrase) {
        ArrayList<String> tokens = new ArrayList<String>();
        TokenStream stream;
		try {
			stream = new EnglishAnalyzer(Version.LUCENE_46).tokenStream(
			    "someField", new StringReader(phrase));
		

        stream.reset();
        while (stream.incrementToken())
            tokens.add(stream.getAttribute(CharTermAttribute.class).toString());
        stream.end();
        stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return tokens;
    }
	
	void indexSomething() {
        EnglishAnalyzer analyzer = new EnglishAnalyzer(Version.LUCENE_43);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_43, analyzer);
        config.setSimilarity(new CustomSimilarity());

        FSDirectory directory;
		try {
			directory = FSDirectory.open(new File("my-index"));
		
        IndexWriter writer = new IndexWriter(directory, config);
        // ... index something ...
        writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

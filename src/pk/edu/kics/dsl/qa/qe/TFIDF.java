package pk.edu.kics.dsl.qa.qe;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.solr.handler.admin.CollectionHandlerApi;

import java.util.TreeMap;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;
import pk.edu.kics.dsl.qa.util.ValueComparator;

public class TFIDF extends LocalQueryExpansion {

	HashMap<String, Double> termsTFIDF = new HashMap<>();
	
	@Override
	public String getRelevantTerms(Question question, int termsToSelect) {
		try {
			super.init(question);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (String key : dictionary) {
			//TODO: Fix it in case any solution is found for commas (Numbers with comma never found!)
			int docFrequency = BiomedQA.TOTAL_DOCUMENTS;
			if(documentFrequency.containsKey(key)) {
				docFrequency = documentFrequency.get(key);
			}
			
			int termFrequency = relevantTermsTotalFrequency.get(key);
			termsTFIDF.put(key, computeTfIdfWeight(termFrequency, docFrequency, BiomedQA.DOCUMENTS_FOR_QE));
		}
		
		Map<String, Double> sortedTermsTFIDF = CollectionHelper.sortByComparator(termsTFIDF, false);
		return CollectionHelper.getTopTerms(sortedTermsTFIDF, termsToSelect);
	}
	
	

	private double computeTfIdfWeight(int termFreq, int DocFrequency, int numDocs) {
		long tfidf = (long) (Math.log(termFreq + 1) * Math.log(numDocs/DocFrequency));
		return tfidf;
	}
}

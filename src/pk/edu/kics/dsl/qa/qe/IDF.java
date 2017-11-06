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

public class IDF extends LocalQueryExpansion {

	HashMap<String, Double> termsIDF = new HashMap<>();
	
	@Override
	public Map<String, Double> getRelevantTerms(Question question) {
		try {
			super.init(question);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (String key : localDictionary) {
			//TODO: Fix it in case any solution is found for commas (Numbers with comma never found!)
			int docFrequency = BiomedQA.TOTAL_DOCUMENTS;
			if(documentFrequency.containsKey(key)) {
				docFrequency = documentFrequency.get(key);
			}
			
			termsIDF.put(key, Math.log((double)BiomedQA.TOTAL_DOCUMENTS/docFrequency));
		}
		
		return CollectionHelper.sortByComparator(termsIDF, false);
	}
	
}

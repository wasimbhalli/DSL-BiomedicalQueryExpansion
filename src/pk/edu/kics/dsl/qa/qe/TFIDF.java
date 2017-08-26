package pk.edu.kics.dsl.qa.qe;

import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class TFIDF extends LocalQueryExpansion {

	HashMap<String, Double> termsTFIDF = new HashMap<>();
	
	@Override
	public String getRelevantTerms(Question question, int termsToSelect) {
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
			
			for (int i = 0; i < BiomedQA.DOCUMENTS_FOR_QE; i++) {
				Map<String, Integer> documentTerms = documentTermFrequencies.get(i);
				
				int termFrequency = 0;
				if(documentTerms.containsKey(key)) {
					termFrequency = documentTerms.get(key);
				}
				
				double previousWeight = 0; 
				
				if(termsTFIDF.containsKey(key)) {
					previousWeight = termsTFIDF.get(key);
				}
				
				double newTermWeight = computeTfIdfWeight(termFrequency, docFrequency, BiomedQA.TOTAL_DOCUMENTS);
				
				termsTFIDF.put(key, previousWeight + newTermWeight );
			}
		}
		
		for (String key : localDictionary) {
			termsTFIDF.put(key, termsTFIDF.get(key)/BiomedQA.DOCUMENTS_FOR_QE);
		}
		
		Map<String, Double> sortedTermsTFIDF = CollectionHelper.sortByComparator(termsTFIDF, false);
		return CollectionHelper.getTopTerms(sortedTermsTFIDF, termsToSelect);
	}
	
	

	private double computeTfIdfWeight(int termFreq, int DocFrequency, int numDocs) {
		long tfidf = (long) (Math.log(termFreq + 1) * Math.log(numDocs/DocFrequency));
		return tfidf;
	}
}

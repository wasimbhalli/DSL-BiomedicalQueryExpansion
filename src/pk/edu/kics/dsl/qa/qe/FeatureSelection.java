package pk.edu.kics.dsl.qa.qe;

import java.util.HashMap;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;

public class FeatureSelection extends LocalQueryExpansion {

	HashMap<String, Integer> truePositive = new HashMap<>();
	HashMap<String, Integer> falsePositive = new HashMap<>();
	HashMap<String, Integer> falseNegative = new HashMap<>();
	HashMap<String, Integer> trueNegative = new HashMap<>();

	public void init(Question question) {
		try {
			super.init(question);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Count the number of document where term is present
		for(String term: localDictionary) {			
			for (int i = 0; i < resultsList.size(); i++) {
				HashMap<String,Integer> TermsFrequency = (HashMap<String,Integer>) documentTermFrequencies.get(i);
				if(TermsFrequency.containsKey(term)) {
					if(truePositive.containsKey(term)) {
						truePositive.put(term, truePositive.get(term) + 1);
					} else {
						truePositive.put(term, 1);
					}
				}
			}
		}

		// Count the number of documents in corpus where term is present
		for(String term: localDictionary) {
			int truePositiveScore = 0;
			int docFrequency = 0;
			if(truePositive.containsKey(term)) {
				truePositiveScore = truePositive.get(term);
			}

			if(documentFrequency.containsKey(term)) {
				docFrequency = documentFrequency.get(term);
			}
			falsePositive.put(term, docFrequency - truePositiveScore);
		}

		// Count documents where term is absent
		for(String key: localDictionary) {
			int truePositiveScore = 0;
			if(truePositive.containsKey(key)) truePositiveScore = truePositive.get(key);
			falseNegative.put(key, BiomedQA.DOCUMENTS_FOR_QE - truePositiveScore);
		}


		// Count corpus documents where term is absent
		for(String key: localDictionary) {
			int falsePositiveScore = 0;
			if(falsePositive.containsKey(key)) falsePositiveScore = falsePositive.get(key);
			trueNegative.put(key, BiomedQA.TOTAL_DOCUMENTS - falsePositiveScore);
		}

	}

	// Override method in child class for implementation of scoring
	@Override
	public String getRelevantTerms(Question question, int termsToSelect) {
		return null;
	}

}

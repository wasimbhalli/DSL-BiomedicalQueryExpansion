package pk.edu.kics.dsl.qa.qe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;
import pk.edu.kics.dsl.qa.util.StringHelper;

public class CoJaccard extends Cooccurrence {

	HashMap<String, HashMap<String, Double>> jaccard = new HashMap<>();
	
	@Override
	public String getRelevantTerms(Question question, int termsToSelect) {
		try {
			super.init(question);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ArrayList<String> questionTerms = StringHelper.analyzeContent(question.getQuestion(), false);

			for(String questionKey: questionTerms) {
				HashMap<String, Integer> dictionaryVector = termsCooccurance.get(questionKey);
				for (String dictionaryKey: dictionaryVector.keySet()) {
					calculateJaccard(questionKey, dictionaryKey, dictionaryVector.get(dictionaryKey));
				}
			}

			for(String questionKey: questionTerms) {
				HashMap<String, Double> dictionaryVector = jaccard.get(questionKey);
				for (String dictionaryKey: dictionaryVector.keySet()) {
					calculateCoDegree(questionKey, dictionaryKey, dictionaryVector.get(dictionaryKey));
				}
			}
			
			calculateFinalScore(questionTerms, termsScore);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, Double> sortedTerms = CollectionHelper.sortByComparator(termsScore, false);
		return CollectionHelper.getTopTerms(sortedTerms, termsToSelect);
	}

	private void calculateJaccard(String questionKey, String dictionaryKey, int Cij) {
		int Ci = 0, Cj = 0;
		
		if(localDocumentFrequency.containsKey(questionKey)) Ci = localDocumentFrequency.get(questionKey);
		if(localDocumentFrequency.containsKey(dictionaryKey)) Cj = localDocumentFrequency.get(dictionaryKey); 

		double jaccardVal = (double) Cij / (Ci + Cj - Cij);

		HashMap<String, Double> inner = jaccard.get(questionKey);
		
		if(inner == null){
			inner = new HashMap<String, Double>();
			inner.put(dictionaryKey, jaccardVal);
		}
		inner.put(dictionaryKey, jaccardVal);
		
		jaccard.put(questionKey, inner);
	}

}

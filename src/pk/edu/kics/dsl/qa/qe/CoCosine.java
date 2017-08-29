package pk.edu.kics.dsl.qa.qe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;
import pk.edu.kics.dsl.qa.util.StringHelper;

public class CoCosine extends Cooccurrence {

	HashMap<String, HashMap<String, Double>> cosine = new HashMap<>();

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
					calculateCosine(questionKey, dictionaryKey, dictionaryVector.get(dictionaryKey));
				}
			}

			for(String questionKey: questionTerms) {
				HashMap<String, Double> dictionaryVector = cosine.get(questionKey);
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

	private void calculateCosine(String questionKey, String dictionaryKey, int Cij) {
		int Ci = 0, Cj = 0;

		if(localDocumentFrequency.containsKey(questionKey)) Ci = localDocumentFrequency.get(questionKey);
		if(localDocumentFrequency.containsKey(dictionaryKey)) Cj = localDocumentFrequency.get(dictionaryKey); 

		double diceValue = (double) Cij / Math.sqrt(Ci * Cj);

		HashMap<String, Double> inner = cosine.get(questionKey);

		if(inner == null){
			inner = new HashMap<String, Double>();
			inner.put(dictionaryKey, diceValue);
		}
		inner.put(dictionaryKey, diceValue);

		cosine.put(questionKey, inner);
	}

}

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
	public Map<String,Double> getRelevantTerms(Question question) {
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

		return CollectionHelper.sortByComparator(termsScore, false);
	}

	private void calculateCosine(String questionKey, String dictionaryKey, int Cij) {
		int Ci = 0, Cj = 0;

		if(localDocumentFrequency.containsKey(questionKey)) Ci = localDocumentFrequency.get(questionKey);
		if(localDocumentFrequency.containsKey(dictionaryKey)) Cj = localDocumentFrequency.get(dictionaryKey); 

		double denominator = Math.sqrt(Ci * Cj);
		
		if(denominator == 0) denominator = 0.00001;
		
		double cosineValue = (double) Cij / denominator;

		HashMap<String, Double> inner = cosine.get(questionKey);

		if(inner == null){
			inner = new HashMap<String, Double>();
			inner.put(dictionaryKey, cosineValue);
		}
		inner.put(dictionaryKey, cosineValue);

		cosine.put(questionKey, inner);
	}

}

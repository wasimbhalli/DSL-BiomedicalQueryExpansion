package pk.edu.kics.dsl.qa.qe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;
import pk.edu.kics.dsl.qa.util.StringHelper;

public class CoDice extends Cooccurrence {

	HashMap<String, HashMap<String, Double>> dice = new HashMap<>();

	@Override
	public Map<String, Double> getRelevantTerms(Question question,int docCount) {
		try {
			super.init(question,docCount);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ArrayList<String> questionTerms = StringHelper.analyzeContent(question.getQuestion(), false);

			for(String questionKey: questionTerms) {
				HashMap<String, Integer> dictionaryVector = termsCooccurance.get(questionKey);
				for (String dictionaryKey: dictionaryVector.keySet()) {
					calculateDice(questionKey, dictionaryKey, dictionaryVector.get(dictionaryKey));
				}
			}

			for(String questionKey: questionTerms) {
				HashMap<String, Double> dictionaryVector = dice.get(questionKey);
				for (String dictionaryKey: dictionaryVector.keySet()) {
					calculateCoDegree(questionKey, dictionaryKey, dictionaryVector.get(dictionaryKey),docCount);
				}
			}

			calculateFinalScore(questionTerms, termsScore);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return CollectionHelper.sortByComparator(termsScore, false);
	}

	private void calculateDice(String questionKey, String dictionaryKey, int Cij) {
		int Ci = 0, Cj = 0;

		if(localDocumentFrequency.containsKey(questionKey)) Ci = localDocumentFrequency.get(questionKey);
		if(localDocumentFrequency.containsKey(dictionaryKey)) Cj = localDocumentFrequency.get(dictionaryKey); 

		double denominator = Ci + Cj;
		
		if(denominator == 0) denominator = 0.00001;
		
		double diceValue = (double) 2 * Cij / denominator ;

		HashMap<String, Double> inner = dice.get(questionKey);

		if(inner == null){
			inner = new HashMap<String, Double>();
			inner.put(dictionaryKey, diceValue);
		}
		inner.put(dictionaryKey, diceValue);

		dice.put(questionKey, inner);
	}
}
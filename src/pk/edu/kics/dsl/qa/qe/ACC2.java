package pk.edu.kics.dsl.qa.qe;

import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class ACC2 extends FeatureSelection {

	HashMap<String, Double> truePositiveRate = new HashMap<>();
	HashMap<String, Double> falsePositiveRate = new HashMap<>();
	HashMap<String, Double> termsScore = new HashMap<>();

	@Override
	public String getRelevantTerms(Question question, int termsToSelect) {
		try {
			super.init(question);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (String key : truePositive.keySet()) {
			truePositiveRate.put(key, (double) truePositive.get(key)/BiomedQA.DOCUMENTS_FOR_QE);
		}

		for (String key : falsePositive.keySet()) {
			falsePositiveRate.put(key, (double) falsePositive.get(key)/(BiomedQA.TOTAL_DOCUMENTS - BiomedQA.DOCUMENTS_FOR_QE));
		}


		for(String key: localDictionary) {

			double termTPR = 0;
			double termFPR = 0;

			if(truePositiveRate.containsKey(key)) termTPR = truePositiveRate.get(key);
			if(falsePositiveRate.containsKey(key)) termFPR = falsePositiveRate.get(key);

			double score = termTPR - termFPR;
			termsScore.put(key, score);
		}

		Map<String, Double> sortedTermsTFIDF = CollectionHelper.sortByComparator(termsScore, false);
		return CollectionHelper.getTopTerms(sortedTermsTFIDF, termsToSelect);
	}

}

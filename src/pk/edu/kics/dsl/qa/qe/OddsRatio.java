package pk.edu.kics.dsl.qa.qe;

import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class OddsRatio extends FeatureSelection {
	
	HashMap<String, Double> termsScore = new HashMap<>();

	@Override
	public String getRelevantTerms(Question question, int termsToSelect) {
		try {
			super.init(question);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for(String key: localDictionary) {

			double tp = 0;
			double tn = 0;
			double fp = 0;
			double fn = 0;

			if(truePositive.containsKey(key)) tp = truePositive.get(key);
			if(trueNegative.containsKey(key)) tn = trueNegative.get(key);
			if(falsePositive.containsKey(key)) fp = falsePositive.get(key);
			if(falseNegative.containsKey(key)) fn = falseNegative.get(key);

			
			double score = (tp * tn)/(fn * fp);
			termsScore.put(key, score);
		}

		Map<String, Double> sortedTermsTFIDF = CollectionHelper.sortByComparator(termsScore, false);
		return CollectionHelper.getTopTerms(sortedTermsTFIDF, termsToSelect);
	}
}

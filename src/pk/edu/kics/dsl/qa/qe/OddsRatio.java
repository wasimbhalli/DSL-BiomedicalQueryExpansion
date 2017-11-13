package pk.edu.kics.dsl.qa.qe;

import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class OddsRatio extends FeatureSelection {
	
	HashMap<String, Double> termsScore = new HashMap<>();

	@Override
	public Map<String, Double> getRelevantTerms(Question question,int docCount) {
		try {
			super.init(question,docCount);
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

			double denominator = fn * fp;
			
			if(denominator == 0 ) denominator = 0.1;
			
			double score = (tp * tn)/denominator;
			termsScore.put(key, score);
		}

		return CollectionHelper.sortByComparator(termsScore, false);
	}
}

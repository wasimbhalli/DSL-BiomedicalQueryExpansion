package pk.edu.kics.dsl.qa.qe;

import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class ChiSquare extends FeatureSelection {
	
	HashMap<String, Double> termsScore = new HashMap<>();

	@Override
	public Map<String, Double> getRelevantTerms(Question question) {
		try {
			super.init(question);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for(String key: localDictionary) {

			int tp = 0;
			int tn = 0;
			int fp = 0;
			int fn = 0;

			if(truePositive.containsKey(key)) tp = truePositive.get(key);
			if(trueNegative.containsKey(key)) tn = trueNegative.get(key);
			if(falsePositive.containsKey(key)) fp = falsePositive.get(key);
			if(falseNegative.containsKey(key)) fn = falseNegative.get(key);

			double pPos = (double) (tp + fn)/(tp + fp + fn + tn);
			double pNeg = (double) (fp + tn)/(tp + fp + fn + tn);
			
			double cs = t(tp, (tp + fp) * pPos) + t(fn, (fn + tn) * pPos) +
					t(fp, (tp + fp) * pNeg) + t(tn, (fn + tn) * pNeg);
			
			termsScore.put(key, cs);
		}

		return CollectionHelper.sortByComparator(termsScore, false);
	}
	
	private double t(int count, double expected) {
		if(expected == 0) return 0;
		return Math.pow(count - expected, 2)/expected;
	}
	
}

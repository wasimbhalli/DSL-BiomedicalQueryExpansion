package pk.edu.kics.dsl.qa.qe;

import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class ChiSquare extends FeatureSelection {
	
	HashMap<String, Double> termsScore = new HashMap<>();

	@Override
	public String getRelevantTerms(Question question, int termsToSelect) {
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

		Map<String, Double> sortedTermsTFIDF = CollectionHelper.sortByComparator(termsScore, false);
		return CollectionHelper.getTopTerms(sortedTermsTFIDF, termsToSelect);
	}
	
	private double t(int count, double expected) {
		return Math.pow(count - expected, 2)/expected;
	}
	
}

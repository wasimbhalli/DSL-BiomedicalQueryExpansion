package pk.edu.kics.dsl.qa.qe;

import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class IG extends FeatureSelection {
	
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

			double termProbability = tp + fp / BiomedQA.TOTAL_DOCUMENTS;
			int nonRelevantDocuments = BiomedQA.TOTAL_DOCUMENTS - BiomedQA.DOCUMENTS_FOR_QE;
			
			Double ig = getEntropy(BiomedQA.DOCUMENTS_FOR_QE, nonRelevantDocuments) - 
					(termProbability * getEntropy(tp, fp) + 
							(1 - termProbability) * (getEntropy(tn, fn)));
			
			termsScore.put(key, ig);
		}

		Map<String, Double> sortedTermsTFIDF = CollectionHelper.sortByComparator(termsScore, false);
		return CollectionHelper.getTopTerms(sortedTermsTFIDF, termsToSelect);
	}
	
	private double getEntropy(int X, double Y) {
		double e = -(X / (X + Y) * Math.log((X / (X + Y)))) - (Y / (X + Y) * Math.log((Y / (X + Y))));
		return e;
	}
	
}

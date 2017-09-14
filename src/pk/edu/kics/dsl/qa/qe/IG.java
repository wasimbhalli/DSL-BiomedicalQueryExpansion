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

			double tp = 0;
			double tn = 0;
			double fp = 0;
			double fn = 0;

			if(truePositive.containsKey(key)) tp = truePositive.get(key);
			if(trueNegative.containsKey(key)) tn = trueNegative.get(key);
			if(falsePositive.containsKey(key)) fp = falsePositive.get(key);
			if(falseNegative.containsKey(key)) fn = falseNegative.get(key);
			
			if(tp == 0) tp = 0.005;
			if(fp == 0) fp = 0.005;
			if(tn == 0) tn = 0.005;
			if(fn == 0) fn = 0.005;
			
			if(tp == 1) tp = 0.99;
			if(fp == 1) fp = 0.99;
			if(tn == 1) tn = 0.99;
			if(fn == 1) fn = 0.99;

			double termProbability = (double) tp + fp / BiomedQA.TOTAL_DOCUMENTS;
			int nonRelevantDocuments = BiomedQA.TOTAL_DOCUMENTS - resultsList.size();
			
			Double ig = getEntropy(resultsList.size(), nonRelevantDocuments) - 
					(termProbability * getEntropy(tp, fp) + 
							(1 - termProbability) * (getEntropy(tn, fn)));
			
			termsScore.put(key, ig);
		}

		Map<String, Double> sortedTermsTFIDF = CollectionHelper.sortByComparator(termsScore, false);
		return CollectionHelper.getTopTerms(sortedTermsTFIDF, termsToSelect);
	}
	
	private double getEntropy(double X, double Y) {
		double e = -(X / (X + Y) * Math.log((X / (X + Y)))) - (Y / (X + Y) * Math.log((Y / (X + Y))));
		return e;
	}
	
}

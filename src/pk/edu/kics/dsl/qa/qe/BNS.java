package pk.edu.kics.dsl.qa.qe;

import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;
import org.apache.commons.math3.distribution.NormalDistribution;

public class BNS extends FeatureSelection {
	
	NormalDistribution normalDistribution = new NormalDistribution();
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

			double termTPR = 0.0005;
			double termFPR = 0.0005;

			if(truePositiveRate.containsKey(key)) termTPR = truePositiveRate.get(key);
			if(falsePositiveRate.containsKey(key)) termFPR = falsePositiveRate.get(key);

			// Fix for words not appearing in global dictionary - comma words
			if(termFPR<0) {
				termFPR = 0;
			}
			
			double Ftpr = normalDistribution.inverseCumulativeProbability(termTPR);
			double Ffpr = normalDistribution.inverseCumulativeProbability(termFPR);
			
			double score = Math.abs(Ftpr - Ffpr);
			termsScore.put(key, score);
		}

		Map<String, Double> sortedTerms = CollectionHelper.sortByComparator(termsScore, false);
		return CollectionHelper.getTopTerms(sortedTerms, termsToSelect);
	}
}

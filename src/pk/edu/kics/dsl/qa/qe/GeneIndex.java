package pk.edu.kics.dsl.qa.qe;

import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class GeneIndex extends FeatureSelection {


	HashMap<String, Double> truePositiveRate = new HashMap<>();
	HashMap<String, Double> falsePositiveRate = new HashMap<>();
	HashMap<String, Double> termsScore = new HashMap<>();
	
	
	
	
	@Override
	public Map<String, Double> getRelevantTerms(Question question,int docCount) {
		try {
			super.init(question,docCount);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (String key : truePositive.keySet()) {
			truePositiveRate.put(key, (double) truePositive.get(key)/BiomedQA.DOCUMENTS_FOR_QE[docCount]);
			      
		}

		for (String key : falsePositive.keySet()) {
			falsePositiveRate.put(key, (double) falsePositive.get(key)/(BiomedQA.TOTAL_DOCUMENTS - BiomedQA.DOCUMENTS_FOR_QE[docCount]));
		}


		for(String key: localDictionary) {

			double termTPR = 0.0005;
			double termFPR = 0.0005;
            
			double tp=0.0;
			double fp=0.0;
	
			
			
			
			
			if(truePositiveRate.containsKey(key)) termTPR = truePositiveRate.get(key);
			if(falsePositiveRate.containsKey(key)) termFPR = falsePositiveRate.get(key);
			
			if(truePositive.containsKey(key)) tp = truePositiveRate.get(key);
			if(falsePositive.containsKey(key)) fp = falsePositiveRate.get(key);
			
			
     
			double giniScore = Math.pow(termTPR, 2) * Math.pow(tp / (tp + fp), 2) + Math.pow(termFPR, 2) * Math.pow(fp / (tp + fp), 2);
			termsScore.put(key, giniScore);
		}

		return CollectionHelper.sortByComparator(termsScore, false);
	}



















}

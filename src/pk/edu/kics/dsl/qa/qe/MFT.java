package pk.edu.kics.dsl.qa.qe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class MFT extends FeatureSelection {


	HashMap<String, Double> termsScore = new HashMap<>();
	HashMap<String, Double> truePositiveRate = new HashMap<>();
	HashMap<String, Double> falsePositiveRate = new HashMap<>();
	Map<String, Double> sortedTermsScoreAcc = new HashMap<>();
	ArrayList<Double> tprResult = new ArrayList<>();
	ArrayList<Double> fprResult = new ArrayList<>();
	public static Map<String, Double> result = new HashMap<>();
	int conditionMetTermsCount = 0;
	
	
	@Override
	public Map<String, Double> getRelevantTerms(Question question,int docCount) {
		try {
			super.init(question,docCount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	
		 result= CollectionHelper.sortByComparatorInt(localTermsTotalFrequency, false); 
	
	
			
		
		 for (String key : truePositive.keySet()) {
				truePositiveRate.put(key, (double) truePositive.get(key) / BiomedQA.DOCUMENTS_FOR_QE[docCount]);
			}

			for (String key : falsePositive.keySet()) {
				falsePositiveRate.put(key,
						(double) falsePositive.get(key) / (BiomedQA.TOTAL_DOCUMENTS - BiomedQA.DOCUMENTS_FOR_QE[docCount]));
			}

			//int cc = 0;
			for (String key : result.keySet()) {

				double termTPR = 0.00001;
				double termFPR = 0.00001;

				if (truePositiveRate.containsKey(key))
					termTPR = truePositiveRate.get(key);
				if (falsePositiveRate.containsKey(key))
					termFPR = falsePositiveRate.get(key);

				if (termFPR == 0)
					termFPR = 0.00001;
				if (termTPR == 0)
					termTPR = 0.00001;

				if (termFPR == 1)
					termFPR = 0.999;
				if (termTPR == 1)
					termTPR = 0.999;

				tprResult.add(termTPR);
				fprResult.add(termFPR);

				double score = termTPR - termFPR;
				//if(score>0.6)
				{	sortedTermsScoreAcc.put(key, score);
			      	conditionMetTermsCount++;
				 }
				/*if (cc++ == 10) {
					break;
				}*/

			}
		
		
		
		
		
		
	
			//ExcelWriterPOI.writeResults(sortedTermsScoreAcc, tprResult, fprResult, 1);
		
		
		
		
		     return sortedTermsScoreAcc;
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	
	}

	
}//class

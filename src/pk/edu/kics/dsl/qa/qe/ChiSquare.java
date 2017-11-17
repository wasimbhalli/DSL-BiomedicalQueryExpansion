package pk.edu.kics.dsl.qa.qe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class ChiSquare extends FeatureSelection {
	
	HashMap<String, Double> termsScore = new HashMap<>();
	Map<String, Double> sortedTermsScoreAcc = new HashMap<>();
	HashMap<String, Double> truePositiveRate = new HashMap<>();
	HashMap<String, Double> falsePositiveRate = new HashMap<>();

	ArrayList<Double> tprResult = new ArrayList<>();
	ArrayList<Double> fprResult = new ArrayList<>();


	@Override
	public Map<String, Double> getRelevantTerms(Question question,int docCount) {
		try {
			super.init(question,docCount);
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
		
		
		
        sortedTermsScoreAcc=CollectionHelper.sortByComparator(termsScore, false);
		
		
		for (String key : truePositive.keySet()) {
			truePositiveRate.put(key, (double) truePositive.get(key) / BiomedQA.DOCUMENTS_FOR_QE[docCount]);
		}

		for (String key : falsePositive.keySet()) {
			falsePositiveRate.put(key,
					(double) falsePositive.get(key) / (BiomedQA.TOTAL_DOCUMENTS - BiomedQA.DOCUMENTS_FOR_QE[docCount]));
		}

		int cc = 0;
		for (String key : sortedTermsScoreAcc.keySet()) {

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

			
			if (cc++ == 9) {
				break;
			}
		}
		
		
		
		
		
		
		ExcelWriterPOI.writeResults(sortedTermsScoreAcc, tprResult, fprResult, 1);


		
		
		
		
		
		
		
		
		
		
		
		
		
		
		

		return CollectionHelper.sortByComparator(termsScore, false);
	}
	
	private double t(int count, double expected) {
		if(expected == 0) return 0;
		return Math.pow(count - expected, 2)/expected;
	}
	
}

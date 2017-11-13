package pk.edu.kics.dsl.qa.qe;

import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class PoisonRatio extends FeatureSelection {


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
			double tn=0.0;
			double fn=0.0;
	
			
			
			
			
			if(truePositiveRate.containsKey(key)) termTPR = truePositiveRate.get(key);
			if(falsePositiveRate.containsKey(key)) termFPR = falsePositiveRate.get(key);
			
			if(truePositive.containsKey(key)) tp = truePositiveRate.get(key);
			if(falsePositive.containsKey(key)) fp = falsePositiveRate.get(key);
			
			if(trueNegative.containsKey(key)) tn = truePositiveRate.get(key);
			if(falseNegative.containsKey(key)) fn = truePositiveRate.get(key);
			
     
			
			
			double lembda = (tp + fp) / (tp + fp + tn + fn);

		       double aij = tp;
		        double bij = fn;
		        double cij = fp;
		        double dij = tn;
		        double aij_ =  BiomedQA.DOCUMENTS_FOR_QE[docCount] * (1 - Math.exp(-lembda));
		        double bij_ = BiomedQA.DOCUMENTS_FOR_QE[docCount] * (Math.exp(-lembda));
		        double cij_ = BiomedQA.TOTAL_DOCUMENTS * (1 - Math.exp(-lembda));
		        double dij_ = BiomedQA.TOTAL_DOCUMENTS * (Math.exp(-lembda));
//		        System.out.println("aij :" + aij);
//		        System.out.println("bij :" +bij);
//		        System.out.println("cij :" +cij);
//		        System.out.println("dij :" +dij);
//		        System.out.println("aij_ :" + aij_);
//		        System.out.println("bij_ :" +bij_);
//		        System.out.println("cij_ :" +cij_);
//		        System.out.println("dij_ :" +dij_);
//		        System.out.println("lembda :" +lembda);
		  
		       termTPR = tp / BiomedQA.DOCUMENTS_FOR_QE[docCount];
		        termFPR = fp / BiomedQA.TOTAL_DOCUMENTS;

		       double poisonScore = (Math.pow((aij - aij_), 2) / aij_) + (Math.pow((bij - bij_), 2) / bij_) + (Math.pow((cij - cij_), 2) / cij_) + (Math.pow((dij - dij_), 2) / dij_);
		    
			termsScore.put(key, poisonScore);
		}

		return CollectionHelper.sortByComparator(termsScore, false);
	}



















}

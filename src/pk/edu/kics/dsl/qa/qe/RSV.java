package pk.edu.kics.dsl.qa.qe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class RSV extends FeatureSelection {

	HashMap<String, Double> termsScore= new HashMap<>();
	HashMap<String, Double> truePositiveRate = new HashMap<>();
	HashMap<String, Double> falsePositiveRate = new HashMap<>();
	Map<String, Double> sortedTermsScoreRSV = new HashMap<>();
	ArrayList<Double> tprResult=new ArrayList<>();
	ArrayList<Double> fprResult=new ArrayList<>();
	public static Map<String, Double> result=new HashMap<>();
	int check=0;

	@Override
	public Map<String, Double> getRelevantTerms(Question question) {
		try {
			super.init(question);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int totalTermFrequency =  0;

		for (String key : localTermsTotalFrequency.keySet()) { 
			totalTermFrequency += localTermsTotalFrequency.get(key);;
		}

		for(String term:localDictionary) 
		{
			double termProbabilityinNonRelevant = 0;
			double termProbabilityinRelevant = (double) localTermsTotalFrequency.get(term)/totalTermFrequency;

			if(termsTotalFrequency.containsKey(term)) {
				termProbabilityinNonRelevant = (double) (termsTotalFrequency.get(term)-localTermsTotalFrequency.get(term))/(totalCorpusTermsFrquency-totalTermFrequency);
			}
			
			double denominator = 0.000001;
			
			if(termProbabilityinNonRelevant!=0 && (1-termProbabilityinRelevant)!= 0) {
				denominator = (termProbabilityinNonRelevant*(1-termProbabilityinRelevant));
			}

			double score =  Math.log((termProbabilityinRelevant*(1-termProbabilityinNonRelevant))/denominator);
			termsScore.put(term, score * (termProbabilityinRelevant-termProbabilityinNonRelevant));

		}

		
		
		result= CollectionHelper.sortByComparator(termsScore, false); 
	
	
		for (String key : truePositive.keySet()) {
			truePositiveRate.put(key, (double) truePositive.get(key)/BiomedQA.DOCUMENTS_FOR_QE);
		}

		for (String key : falsePositive.keySet()) {
			falsePositiveRate.put(key, (double) falsePositive.get(key)/(BiomedQA.TOTAL_DOCUMENTS - BiomedQA.DOCUMENTS_FOR_QE));
		}

		
		
		
         int cc=0;
		for(String key: result.keySet()) {

            
			double termTPR = 0.00001;
			double termFPR = 0.00001;

			if(truePositiveRate.containsKey(key)) termTPR = truePositiveRate.get(key);
			if(falsePositiveRate.containsKey(key)) termFPR = falsePositiveRate.get(key);

			if(termFPR==0) termFPR = 0.00001;
			if(termTPR==0) termTPR = 0.00001;

			if(termFPR == 1) termFPR = 0.999;
			if(termTPR == 1) termTPR = 0.999;

			
			tprResult.add(termTPR);
			fprResult.add(termFPR);
			
			double score = termTPR -termFPR;
			if(score>0.6)
			 { sortedTermsScoreRSV.put(key, score);
			    check++;
			   
			    
			 }
			
			if(cc++==19)
				{
				   System.out.println();
				   break;
				}
			
		}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	System.out.println("check="+check);
	
	return sortedTermsScoreRSV;
	
	
	
	
	
	}//method







}//class

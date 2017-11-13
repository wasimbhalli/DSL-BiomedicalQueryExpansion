package pk.edu.kics.dsl.qa.qe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class KLDivergence extends FeatureSelection {

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

		int totalTermFrequency = 0;

		for (String key : localTermsTotalFrequency.keySet()) {
			totalTermFrequency += localTermsTotalFrequency.get(key);
			;
		}

		for (String term : localDictionary) {

			double termProbabilityinCorpus = 0.000001;
			double termProbabilityinRelevant = (double) localTermsTotalFrequency.get(term) / totalTermFrequency;

			if (termsTotalFrequency.containsKey(term)) {
				termProbabilityinCorpus = (double) termsTotalFrequency.get(term) / totalCorpusTermsFrquency;
			} else {
				termProbabilityinCorpus = 0.00001;
			}

			if (termProbabilityinCorpus == 1) {
				termProbabilityinCorpus = 0.999;
			}

			double KLDScore = termProbabilityinRelevant * Math.log(termProbabilityinRelevant / termProbabilityinCorpus);

			termsScore.put(term, KLDScore);
		}

		result = CollectionHelper.sortByComparator(termsScore, false);

		// trp //frp

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
			/*if (cc++ == 9) {
				break;
			}*/

		}

		/*
		 * int counter=0; for (String string : result.keySet()) {
		 * 
		 * System.out.print("key=="+string); System.out.print("\t");
		 * System.out.print(tprResult.get(counter)); System.out.print("\t");
		 * System.out.print(fprResult.get(counter)); System.out.print("\t"+ "\t");
		 * System.out.println(tprResult.get(counter)-fprResult.get(counter));
		 * 
		 * //System.out.print(tprResult.get(counter)-fprResult.get(counter));
		 * System.out.println(); if(counter++==10) break;
		 * 
		 * }
		 */

		sortedTermsScoreAcc = CollectionHelper.sortByComparator(sortedTermsScoreAcc, false);
		System.out.println("conditionMetTermsCount=" + conditionMetTermsCount);
		
		
		
		System.out.println("size="+tprResult.size()+","+fprResult.size()+","+sortedTermsScoreAcc.size());
		ExcelWriterPOI.writeResults(sortedTermsScoreAcc, tprResult, fprResult, 1);
		
		
		
		return sortedTermsScoreAcc;
		// return result;

	}

	/*
	 * public HashMap<String, Double> getSortedTopTenTerms() {
	 * 
	 * return result; HashMap<String, Double>toptenmap=new HashMap<>(); for (String
	 * string : result.keySet()) { toptenmap.put(key,
	 * value)=result.entrySet().iterator().next()
	 * 
	 * 
	 * }
	 * 
	 * 
	 * }
	 */

}
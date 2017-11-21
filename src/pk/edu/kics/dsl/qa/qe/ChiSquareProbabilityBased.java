package pk.edu.kics.dsl.qa.qe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class ChiSquareProbabilityBased extends FeatureSelection {

	HashMap<String, Double> termsScore = new HashMap<>();
	Map<String, Double> sortedTermsScoreAcc = new HashMap<>();
	HashMap<String, Double> truePositiveRate = new HashMap<>();
	HashMap<String, Double> falsePositiveRate = new HashMap<>();

	Map<Integer, String> missingTerms=new HashMap<>();
	ArrayList<Double> tprResult = new ArrayList<>();
	ArrayList<Double> fprResult = new ArrayList<>();
	int counter=0;
	int queryCount=1;

	@Override
	public Map<String, Double> getRelevantTerms(Question question,int docCount) {
		try {
			super.init(question,docCount);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int totalTermFrequency =  0;

		for (String key : localTermsTotalFrequency.keySet()) { 
			totalTermFrequency += localTermsTotalFrequency.get(key);;
		}

		for(String term:localDictionary) {
			
			double termProbabilityinCorpus = 0;
			double termProbabilityinRelevant = (double) localTermsTotalFrequency.get(term)/totalTermFrequency;
			
			if(termsTotalFrequency.containsKey(term)) {
				termProbabilityinCorpus = (double) termsTotalFrequency.get(term)/totalCorpusTermsFrquency;
			} else {
				counter++;
				System.out.println("query"+counter+"missing terms=");
				missingTerms.put(queryCount, term);
				System.out.println("terms are="+term);
				termProbabilityinCorpus = 0.000001;
			}
			
			
			double ChiSquare = Math.pow(termProbabilityinRelevant - termProbabilityinCorpus, 2)/termProbabilityinCorpus;

			//boolean ifAtleastOneAlphabet=term.matches(".*[a-zA-Z]+.*");
			//boolean checkNum=term.matches(".*\\d+.*");
			//if(ifAtleastOneAlphabet) //&& !checkNum
			{	termsScore.put(term, ChiSquare );
			}
		}

		queryCount++;
		
		
		System.out.println("counter="+counter);
		
		
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

		
		System.out.println(".....................");
		
		Arrays.asList(missingTerms);
		

		
		
		
		
		
		
		
		
		
		
		
		
		return CollectionHelper.sortByComparator(termsScore, false);
	}
}
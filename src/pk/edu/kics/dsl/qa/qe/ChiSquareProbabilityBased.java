package pk.edu.kics.dsl.qa.qe;

import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class ChiSquareProbabilityBased extends LocalQueryExpansion {

	HashMap<String, Double> termsScore = new HashMap<>();

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

		for(String term:localDictionary) {
			
			double termProbabilityinCorpus = 0;
			double termProbabilityinRelevant = (double) localTermsTotalFrequency.get(term)/totalTermFrequency;
			
			if(termsTotalFrequency.containsKey(term)) {
				termProbabilityinCorpus = (double) termsTotalFrequency.get(term)/totalCorpusTermsFrquency;
			} else {
				termProbabilityinCorpus = 0.000001;
			}
			
			double ChiSquare = Math.pow(termProbabilityinRelevant - termProbabilityinCorpus, 2)/termProbabilityinCorpus;

			termsScore.put(term, ChiSquare );
		}


		return CollectionHelper.sortByComparator(termsScore, false);
	}
}
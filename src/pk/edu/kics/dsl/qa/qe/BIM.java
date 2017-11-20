package pk.edu.kics.dsl.qa.qe;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.distribution.NormalDistribution;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class BIM extends LocalQueryExpansion {

	HashMap<String, Double> termsScore = new HashMap<>();
	NormalDistribution normalDistribution = new NormalDistribution();

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
			
			double denominator = termProbabilityinCorpus * (1-termProbabilityinRelevant);
			if(denominator == 0) denominator = 0.001;
					
			double bim = termProbabilityinRelevant * (1- termProbabilityinCorpus) / denominator;

			termsScore.put(term, bim );
		}


		return CollectionHelper.sortByComparator(termsScore, false);
	}
}
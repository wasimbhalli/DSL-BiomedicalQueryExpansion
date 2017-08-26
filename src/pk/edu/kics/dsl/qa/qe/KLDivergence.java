package pk.edu.kics.dsl.qa.qe;

import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class KLDivergence extends LocalQueryExpansion {

	HashMap<String, Double> termsScore = new HashMap<>();

	@Override
	public String getRelevantTerms(Question question, int termsToSelect) {
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
			
			double termProbabilityinRelevant = (double) localTermsTotalFrequency.get(term)/totalTermFrequency;
			double termProbabilityinCorpus = (double) termsTotalFrequency.get(term)/totalCorpusTermsFrquency;
			
			double KLDScore = termProbabilityinRelevant * Math.log(termProbabilityinRelevant/termProbabilityinCorpus);

			termsScore.put(term, KLDScore );
		}


		Map<String, Double> sortedTermsTFIDF = CollectionHelper.sortByComparator(termsScore, false);
		return CollectionHelper.getTopTerms(sortedTermsTFIDF, termsToSelect);
	}

}

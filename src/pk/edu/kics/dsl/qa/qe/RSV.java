package pk.edu.kics.dsl.qa.qe;

import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class RSV extends LocalQueryExpansion {

	HashMap<String, Double> termsScore= new HashMap<>();

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

		Map<String,Double> sortedScore = CollectionHelper.sortByComparator(termsScore, false); 
		return CollectionHelper.getTopTerms(sortedScore, termsToSelect);
	}


}

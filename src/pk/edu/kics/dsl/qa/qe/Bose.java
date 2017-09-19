package pk.edu.kics.dsl.qa.qe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;
import pk.edu.kics.dsl.qa.util.QEHelper;
import pk.edu.kics.dsl.qa.util.StringHelper;

public class Bose extends LocalQueryExpansion 
{
	HashMap<String, Double> termsScore = new HashMap<>();

	@Override
	public String getRelevantTerms(Question question, int termsToSelect) {
		try {
			super.init(question);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for(String key:localDictionary) {
			int tfx = localTermsTotalFrequency.get(key);
			double Pn = 0.0001;
			
			if(termsTotalFrequency.containsKey(key)) {
				Pn = termsTotalFrequency.get(key)/BiomedQA.TOTAL_DOCUMENTS;
			}
			
			double score = tfx * log((1+Pn)/Pn, 2) + Math.log(1 + Pn);
			termsScore.put(key, score);
		}

		Map<String, Double> sortedTerms = CollectionHelper.sortByComparator(termsScore, false);
		return CollectionHelper.getTopTerms(sortedTerms, termsToSelect);
	}

	private int log(double x, int base)
	{
		return (int) (Math.log(x) / Math.log(base));
	}
}
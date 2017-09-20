package pk.edu.kics.dsl.qa.qe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;
import pk.edu.kics.dsl.qa.util.QEHelper;
import pk.edu.kics.dsl.qa.util.StringHelper;

public class LRF extends LocalQueryExpansion 
{
	HashMap<String, Double> termsScore = new HashMap<>();

	@Override
	public Map<String, Double> getRelevantTerms(Question question) {
		try {
			super.init(question);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for(String key:localDictionary) {
			double Ptg = 0;
			double lambda = 0.6;
			double score = 0;
			
			if(termsTotalFrequency.containsKey(key)) {
				Ptg = (double)termsTotalFrequency.get(key)/totalCorpusTermsFrquency;
			}
			
			for (int i = 0; i < resultsList.size(); i++) {
				Map<String, Integer> doc = documentTermFrequencies.get(i);
				int TF = 0;
				if(doc.containsKey(key)) TF = doc.get(key);
				int totalTF = documentTotalTerms.get(i);
				double PtGivenMr = lambda * ((double)TF / totalTF) + (1 - lambda) * Ptg;
				score += Math.log(PtGivenMr/Ptg);
			}
			
			if(Double.isNaN(score)) score = 0;
			
			termsScore.put(key, score);
		}

		return CollectionHelper.sortByComparator(termsScore, false);
	}

}
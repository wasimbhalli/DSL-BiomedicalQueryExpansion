package pk.edu.kics.dsl.qa.qe;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.distribution.NormalDistribution;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class SF extends LocalQueryExpansion {

	HashMap<String, Double> termsScore = new HashMap<>();
	NormalDistribution normalDistribution = new NormalDistribution();

	@Override
	public Map<String, Double> getRelevantTerms(Question question) {
		try {
			super.init(question);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for(String term:localDictionary) {
			
			int localDF = 0;
			if(localDocumentFrequency.containsKey(term)) {
				localDF = localDocumentFrequency.get(term);
			}
			
			int docFrequency = BiomedQA.TOTAL_DOCUMENTS;
			if(documentFrequency.containsKey(term)) {
				docFrequency = documentFrequency.get(term);
			}
			
			double finalDocFrequency = Math.log((double)BiomedQA.TOTAL_DOCUMENTS/docFrequency);
					
			double score = Math.log(localDF) * finalDocFrequency;

			termsScore.put(term, score);
		}


		return CollectionHelper.sortByComparator(termsScore, false);
	}
}
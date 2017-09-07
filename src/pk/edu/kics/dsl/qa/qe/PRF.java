package pk.edu.kics.dsl.qa.qe;

import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class PRF extends LocalQueryExpansion 
{
	HashMap<String, Double> termsScore= new HashMap<>();

	@Override
	public String getRelevantTerms(Question question, int termsToSelect) {
		try {
			super.init(question);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for(String term:localDictionary) 
		{

			double pNonRelated = 0;
			double pRelated = (double) localDocumentFrequency.get(term)/BiomedQA.DOCUMENTS_FOR_QE;

			if(termsTotalFrequency.containsKey(term)) {
				pNonRelated = (double) (documentFrequency.get(term)-localDocumentFrequency.get(term))/(BiomedQA.TOTAL_DOCUMENTS-BiomedQA.DOCUMENTS_FOR_QE);
			} else {
				pNonRelated = 0.000001;
			}

			termsScore.put(term, pRelated/pNonRelated);
		}
		
		Map<String,Integer> terms = CollectionHelper.sortByComparatorInt(localTermsTotalFrequency, false); 
		return CollectionHelper.getTopTerms(terms, termsToSelect);
	}
}

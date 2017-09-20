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
	public Map<String, Double> getRelevantTerms(Question question) {
		try {
			super.init(question);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for(String term:localDictionary) 
		{
			double Pnr = 0.0000001;
			int relevantDocuments = resultsList.size();
			int localTermDf = localDocumentFrequency.get(term);
			double Pr = (double) localTermDf/relevantDocuments;

			if(documentFrequency.containsKey(term)) {
				int termDf = documentFrequency.get(term);
				int totalNonRelevantDocuments = BiomedQA.TOTAL_DOCUMENTS- relevantDocuments;
				double temp = (double) (termDf - localTermDf)/totalNonRelevantDocuments;
				if(temp!=0) Pnr = temp;
			}

			termsScore.put(term, Pr/Pnr);
		}

		return CollectionHelper.sortByComparator(termsScore, false); 
	}
}
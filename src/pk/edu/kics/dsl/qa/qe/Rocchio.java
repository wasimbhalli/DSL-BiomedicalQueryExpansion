package pk.edu.kics.dsl.qa.qe;

import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class Rocchio extends IdeRegular {

	HashMap<String, Double> termsScore= new HashMap<>();
	private final int P = 1; 
	private final int M = 2;

	@Override
	public String getRelevantTerms(Question question, int termsToSelect) {
		super.init(question);

		for(String key: localDictionary) {
			double qTFIDF = 0, dTFIDF = 0;
			if(questionTFIDFVector.containsKey(key)) qTFIDF = questionTFIDFVector.get(key);
			if(relevantDocumentsTFIDFVector.containsKey(key)) dTFIDF = relevantDocumentsTFIDFVector.get(key) / BiomedQA.DOCUMENTS_FOR_QE;
			termsScore.put(key, P * qTFIDF + M * dTFIDF);
		}

		Map<String, Double> sortedTermsTFIDF = CollectionHelper.sortByComparator(termsScore, false);
		return CollectionHelper.getTopTerms(sortedTermsTFIDF, termsToSelect);
	}
}
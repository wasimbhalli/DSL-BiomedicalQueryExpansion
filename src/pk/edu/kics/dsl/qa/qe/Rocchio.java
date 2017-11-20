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
	public Map<String, Double> getRelevantTerms(Question question) {
		super.init(question);

		for(String key: localDictionary) {
			double qTFIDF = 0, dTFIDF = 0;
			if(questionTFIDFVector.containsKey(key)) qTFIDF = questionTFIDFVector.get(key);
			if(relevantDocumentsTFIDFVector.containsKey(key)) dTFIDF = relevantDocumentsTFIDFVector.get(key);
			termsScore.put(key, P * qTFIDF + M * dTFIDF);
		}

		return CollectionHelper.sortByComparator(termsScore, false);
	}
}
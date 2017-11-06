package pk.edu.kics.dsl.qa.qe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;
import pk.edu.kics.dsl.qa.util.QEHelper;
import pk.edu.kics.dsl.qa.util.StringHelper;

public class IdeRegular extends LocalQueryExpansion 
{
	HashMap<String, Double> termsScore = new HashMap<>();
	HashMap<String, Double> relevantDocumentsTFIDFVector = new HashMap<>();
	HashMap<String, Double> questionTFIDFVector = new HashMap<>();

	public void init(Question question) {
		try 
		{
			super.init(question);
			
			// get the sum of all tfidf scores for each retrieved document.
			relevantDocumentsTFIDFVector = QEHelper.getTermsTFIDF(localDictionary, documentFrequency, documentTermFrequencies);
			
			// get the vector for question
			ArrayList<String> questionDictionary = StringHelper.analyzeContent(question.getQuestion(), false);

			// Zero, because question is only a single document!
			Map<Integer, Map<String, Integer>> questionTermsFrequency = new HashMap<Integer, Map<String, Integer>>();
			questionTermsFrequency.put(0, StringHelper.getWordsFrequency(questionDictionary));
			
			// Now, build question TFIDF vector
			questionTFIDFVector = QEHelper.getTermsTFIDF(questionDictionary, documentFrequency, questionTermsFrequency);
		
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, Double> getRelevantTerms(Question question) {
		this.init(question);

		for(String key:relevantDocumentsTFIDFVector.keySet()) {
			double newTFIDF = relevantDocumentsTFIDFVector.get(key)*resultsList.size();
			relevantDocumentsTFIDFVector.put(key, newTFIDF);
		}
		
		for(String key: localDictionary) {
			double qTFIDF = 0, dTFIDF = 0;
			if(questionTFIDFVector.containsKey(key)) qTFIDF = questionTFIDFVector.get(key);
			if(relevantDocumentsTFIDFVector.containsKey(key)) dTFIDF = relevantDocumentsTFIDFVector.get(key);
			termsScore.put(key, qTFIDF + dTFIDF);
		}

		return CollectionHelper.sortByComparator(termsScore, false);
	}
}
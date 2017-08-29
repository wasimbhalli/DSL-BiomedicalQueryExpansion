package pk.edu.kics.dsl.qa.qe;

import java.util.ArrayList;
import java.util.HashMap;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.StringHelper;

public class Cooccurrence extends LocalQueryExpansion {
	
	protected HashMap<String, Double> termsScore = new HashMap<>();
	protected HashMap<String, HashMap<String, Integer>> termsCooccurance = new HashMap<>();
	protected HashMap<String, HashMap<String, Double>> coDegree = new HashMap<>();

	@Override
	public void init(Question question) {
		try {
			super.init(question);

			ArrayList<String> questionTerms = StringHelper.analyzeContent(question.getQuestion(), false);

			for(String questionKey: questionTerms) {
				for(String dictionaryKey: localDictionary) {
					for (int docNum = 0; docNum < documentTermFrequencies.size(); docNum++) {
						calculateTermCooccurance(questionKey, dictionaryKey, docNum);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void calculateTermCooccurance(String questionKey, String dictionaryKey, int docNumber) {
		HashMap<String,Integer> documentTermsFrequency = 
				(HashMap<String,Integer>) documentTermFrequencies.get(docNumber);
		boolean bothTermsAvailable = documentTermsFrequency.containsKey(questionKey) && 
				documentTermsFrequency.containsKey(dictionaryKey);

		if(bothTermsAvailable) {
			HashMap<String, Integer> inner = termsCooccurance.get(questionKey);

			if(inner == null){
				inner = new HashMap<String, Integer>();
				inner.put(dictionaryKey, 1);
			}

			int previousScore = 0;
			if(inner.containsKey(dictionaryKey)) previousScore = inner.get(dictionaryKey);
			inner.put(dictionaryKey, previousScore + 1 );
			termsCooccurance.put(questionKey, inner);
		} else {
			HashMap<String, Integer> inner = termsCooccurance.get(questionKey);
			if(inner == null){
				inner = new HashMap<String, Integer>();
				inner.put(dictionaryKey, 0);
			}
			termsCooccurance.put(questionKey, inner);
		}
	}
	
	protected void calculateCoDegree(String questionKey, String dictionaryKey, double coQiC) {
		
		int docFrequency = 0;
		if(documentFrequency.containsKey(dictionaryKey)) docFrequency = documentFrequency.get(dictionaryKey);
		
		double idf = Math.log((double)BiomedQA.TOTAL_DOCUMENTS/docFrequency);
		double coDegreeValue = Math.log(coQiC + 1) * (idf/Math.log(BiomedQA.DOCUMENTS_FOR_QE));
		
		HashMap<String, Double> inner = coDegree.get(questionKey);
		
		if(inner == null){
			inner = new HashMap<String, Double>();
			inner.put(dictionaryKey, coDegreeValue);
		}
		inner.put(dictionaryKey, coDegreeValue);
		
		coDegree.put(questionKey, inner);
	}
	
	protected void calculateFinalScore(ArrayList<String> questionTerms, HashMap<String, Double> termsScore) {
		
		for(String c: localDictionary) {
			
			double finalScore = 1;
			
			for(String qi: questionTerms) {
				HashMap<String, Double> inner = coDegree.get(qi);
				double score = 1;
				if(inner.containsKey(c)) score = inner.get(c);
				finalScore *= score;
			}
			
			termsScore.put(c, finalScore);
		}
	}

}

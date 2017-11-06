package pk.edu.kics.dsl.qa.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.BiomedQA;

public class QEHelper {

	public static HashMap<String, Double> getTermsTFIDF(ArrayList<String> localDictionary, 
			HashMap<String, Integer> documentFrequency, Map<Integer, Map<String, Integer>> documentTermFrequencies)
	{
		HashMap<String, Double> termsTFIDF = new HashMap<>();

		for (String key : localDictionary) {
			//TODO: Fix it in case any solution is found for commas (Numbers with comma never found!)
			int docFrequency = BiomedQA.TOTAL_DOCUMENTS;
			
			if(documentFrequency.containsKey(key)) {
				docFrequency = documentFrequency.get(key);
			}

			for (int i = 0; i < documentTermFrequencies.size(); i++) {
				Map<String, Integer> documentTerms = documentTermFrequencies.get(i);

				int termFrequency = 0;
				if(documentTerms.containsKey(key)) {
					termFrequency = documentTerms.get(key);
				}

				double previousWeight = 0; 

				if(termsTFIDF.containsKey(key)) {
					previousWeight = termsTFIDF.get(key);
				}

				double newTermWeight = computeTfIdfWeight(termFrequency, docFrequency, BiomedQA.TOTAL_DOCUMENTS);

				termsTFIDF.put(key, previousWeight + newTermWeight );
			}
		}

		for (String key : localDictionary) {
			termsTFIDF.put(key, termsTFIDF.get(key)/documentTermFrequencies.size());
		}

		return termsTFIDF;
	}

	private  static double computeTfIdfWeight(int termFreq, int DocFrequency, int numDocs) {
		double tfidf = (Math.log(termFreq + 1) * Math.log((double)numDocs/DocFrequency));
		return tfidf;
	}

}

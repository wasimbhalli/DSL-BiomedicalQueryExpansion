package pk.edu.kics.dsl.qa.qe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;
import pk.edu.kics.dsl.qa.util.StringHelper;

public class WE extends LocalQueryExpansion {
	HashMap<String, Double> termsScore = new HashMap<>();

	@Override
	public String getRelevantTerms(Question question, int termsToSelect) {
		try {
			super.init(question);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ArrayList<String> selectedTerms = getTop30TermsUsingTFIDF();
		
		System.out.println("Done with Phase-I");
		
		try {
			ArrayList<String> query = StringHelper.solrPreprocessor(question.getQuestion());
			System.out.println("Query Terms: " + query.size());
			int counter = 1;
			for(String key: selectedTerms) {
				double score = 0;
				for(String queryKey: query) {
					score += getTermsScore(key, queryKey);
				}
				termsScore.put(key, score);
				System.out.println("Done with term: " + key + " (" + score + ")");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		Map<String, Double> sortedTerms = CollectionHelper.sortByComparator(termsScore, false);
		return CollectionHelper.getTopTerms(sortedTerms, termsToSelect);
	}

	private double getTermsScore(String term1, String term2) {

		double score = 0;
		StringBuilder cmd = new StringBuilder();

		cmd.append("python").append(" ");
		cmd.append("resources/script/termSimilarity.py").append(" ");
		cmd.append(term1).append(" ").append(term2);

		try {
			String scoreValue = execCmd(cmd.toString());
			if(scoreValue != null && !scoreValue.isEmpty()) score = Double.parseDouble(scoreValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return score;
	}
	
	private String execCmd(String cmd) throws java.io.IOException {
	    @SuppressWarnings("resource")
		java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	private ArrayList<String> getTop30TermsUsingTFIDF() {
		HashMap<String, Double> termsTFIDF = new HashMap<>();
		
		for (String key : localDictionary) {
			//TODO: Fix it in case any solution is found for commas (Numbers with comma never found!)
			int docFrequency = BiomedQA.TOTAL_DOCUMENTS;
			if(documentFrequency.containsKey(key)) {
				docFrequency = documentFrequency.get(key);
			}
			
			for (int i = 0; i < BiomedQA.DOCUMENTS_FOR_QE; i++) {
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
			termsTFIDF.put(key, termsTFIDF.get(key)/BiomedQA.DOCUMENTS_FOR_QE);
		}
		// Get top 30 terms
		Map<String, Double> sortedTermsTFIDF = CollectionHelper.sortByComparator(termsTFIDF, false);
		String[] terms = CollectionHelper.getTopTerms(sortedTermsTFIDF, 30).split(" ");
		return new ArrayList<>(Arrays.asList(terms));
	}
	
	private double computeTfIdfWeight(int termFreq, int DocFrequency, int numDocs) {
		double tfidf = (Math.log(termFreq + 1) * Math.log((double)numDocs/DocFrequency));
		return tfidf;
	}
	
}

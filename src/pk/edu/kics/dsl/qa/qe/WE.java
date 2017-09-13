package pk.edu.kics.dsl.qa.qe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;
import pk.edu.kics.dsl.qa.util.IOHelper;
import pk.edu.kics.dsl.qa.util.QEHelper;
import pk.edu.kics.dsl.qa.util.StringHelper;

public class WE extends LocalQueryExpansion {
	HashMap<String, Double> termsScore = new HashMap<>();
	private static ArrayList<String> commonWords = IOHelper.getListFromTextFile("data/3000-common-english-words.txt");

	@Override
	public String getRelevantTerms(Question question, int termsToSelect) {
		try {
			super.init(question);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ArrayList<String> selectedTerms = getTop50TermsUsingMFT();

		ArrayList<String> filteredSelectedTerms = new ArrayList<>(); 
		for(String key: selectedTerms) {
			if(!commonWords.contains(key)) {
				filteredSelectedTerms.add(key);
			}
		}

		ArrayList<String> filteredQuery = new ArrayList<>(); 
		try {
			ArrayList<String> query = StringHelper.solrPreprocessor(question.getQuestion());
			for(String key: query) {
				if(!commonWords.contains(key)) {
					filteredQuery.add(key);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Done with Phase-I");
		System.out.println("Query Terms: " + filteredQuery.size());

		for(String key: filteredSelectedTerms) {
			double score = 0;
			for(String queryKey: filteredQuery) {
				score += getTermsScore(key, queryKey);
			}
			termsScore.put(key, score);
			System.out.println("Done with term: " + key + " (" + score + ")");
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

	private ArrayList<String> getTop50TermsUsingTFIDF() {
		HashMap<String, Double> termsTFIDF = QEHelper.getTermsTFIDF(localDictionary, documentFrequency, documentTermFrequencies);;
		Map<String, Double> sortedTermsTFIDF = CollectionHelper.sortByComparator(termsTFIDF, false);
		String[] terms = CollectionHelper.getTopTerms(sortedTermsTFIDF, 50).split(" ");
		return new ArrayList<>(Arrays.asList(terms));
	}


	private ArrayList<String> getTop50TermsUsingMFT() {
		String[] terms = CollectionHelper.getTopTerms(localTermsTotalFrequency, 50).split(" ");
		return new ArrayList<>(Arrays.asList(terms));
	}
}

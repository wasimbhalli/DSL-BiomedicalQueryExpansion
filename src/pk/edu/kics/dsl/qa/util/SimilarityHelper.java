package pk.edu.kics.dsl.qa.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pk.edu.kics.dsl.qa.BiomedQA;

public class SimilarityHelper {

	public static String applySemanticFiltering(Map<String, Double> termsScore, List<String> query, BiomedQA.SemanticSource semanticSource) {
		
		String result = "";
		
		// Display the terms that will be used for semantic filtering
		//System.out.println(CollectionHelper.getTopTerms(termsScore, BiomedQA.TOP_TERMS_FOR_SEMANTIC_FILTERING));
		//System.out.println("Query Size:" + query.size() + " Terms Size: " + BiomedQA.TOP_TERMS_FOR_SEMANTIC_FILTERING);
		
		if(semanticSource == BiomedQA.SemanticSource.MeSH) {
			result = applyMeSHSemanticFiltering(termsScore, query);
		} else {
			result = applyWESemanticFiltering(termsScore, query);
		}
		
		return result;
	}
	
	public static String applyWESemanticFiltering(Map<String, Double> termsScore, List<String> query) {
		
		HashMap<String, Double> semanticScore = new HashMap<>();
		
		int counter = 1;
		for(String key: termsScore.keySet()) {
			double score = 0;
			for(String queryKey: query) {
				score += getTermsScore(key, queryKey);
			}
			semanticScore.put(key, score);
			if(counter == BiomedQA.TOP_TERMS_FOR_SEMANTIC_FILTERING) break;
			counter++;
		}

		Map<String, Double> sortedSemanticScore = CollectionHelper.sortByComparator(semanticScore, false);

		termsScore = CollectionHelper.normalizeScore(termsScore);
		sortedSemanticScore = CollectionHelper.normalizeScore(sortedSemanticScore);
		
		for (String key: sortedSemanticScore.keySet()) {
			double initialSemanticScore = sortedSemanticScore.get(key);
			double initialTermScore = 0;
			if(termsScore.containsKey(key)) initialTermScore = termsScore.get(key);
			sortedSemanticScore.put(key, initialSemanticScore + initialTermScore);
		}
		
		String relevantTerms=  CollectionHelper.getTopTerms(sortedSemanticScore, BiomedQA.TOP_TERMS_TO_SELECT);

		return relevantTerms;		
	}
	
	private static double getTermsScore(String term1, String term2) {

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

	private static String execCmd(String cmd) throws java.io.IOException {
		@SuppressWarnings("resource")
		java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
	
	public static String applyMeSHSemanticFiltering(Map<String, Double> termsScore, List<String> query) {

		String filePath = "resources/temp.txt";
		PrintWriter pw;

		try {
			pw = new PrintWriter(filePath);
			int counter = 1;
			for(String key:termsScore.keySet()) {
				for(String token: query) {
					pw.write(key + "<>" + token);
					pw.write("\n");
				}
				if(counter == BiomedQA.TOP_TERMS_FOR_SEMANTIC_FILTERING) break;
				counter++;
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		HashMap<String, Double> semanticScore = processBuilder(filePath, query.size());
		Map<String, Double> sortedSemanticScore = CollectionHelper.sortByComparator(semanticScore, false);

		termsScore = CollectionHelper.normalizeScore(termsScore);
		sortedSemanticScore = CollectionHelper.normalizeScore(sortedSemanticScore);
		
		for (String key: sortedSemanticScore.keySet()) {
			double initialSemanticScore = sortedSemanticScore.get(key);
			double initialTermScore = 0;
			if(termsScore.containsKey(key)) initialTermScore = termsScore.get(key);
			sortedSemanticScore.put(key, initialSemanticScore + initialTermScore);
		}
		
		String relevantTerms=  CollectionHelper.getTopTerms(sortedSemanticScore, BiomedQA.TOP_TERMS_TO_SELECT);

		return relevantTerms;
	}

	// The file should have second term similar for a query term i.e. fixed for one function call
	public static HashMap<String, Double> processBuilder(String file, int queryTermsCount)
	{
		HashMap<String, Double> output = new HashMap<String, Double>();
		Runtime rt = Runtime.getRuntime();
		Process proc;

		try {
			proc = rt.exec("perl resources/script/query-umls-similarity-webinterface.pl --measure lch --infile " + file);

			proc.waitFor();
			BufferedReader bfr = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = "";

			int counter = 1;
			double score = 0;

			while ((line = bfr.readLine()) != null) {
				String[] result = line.split("<>");
				String term = result[1];

				if(!result[0].isEmpty()) {
					score += Double.valueOf(result[0]); 
				}

				int index = term.indexOf("(");

				if(index != -1) term = term.substring(0, index);

				if(counter % queryTermsCount == 0) {
					output.put(term, score);
					score = 0;
				}

				counter++;
			}

			bfr.close();

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		return output;
	}
}
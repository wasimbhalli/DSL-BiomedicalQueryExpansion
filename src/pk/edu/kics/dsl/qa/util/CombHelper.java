package pk.edu.kics.dsl.qa.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pk.edu.kics.dsl.qa.BiomedQA;

public class CombHelper {

	public static String intersect(ArrayList<String> termLists) {

		ArrayList<Set> terms = new ArrayList<>();

		for(String list: termLists) {
			terms.add(new HashSet<String>(Arrays.asList(list.split(" "))));
		}

		Set<String> intersection = new HashSet<String>(terms.get(0));

		for (Set termSet : terms) {
			intersection.retainAll(termSet);
		}

		String result = String.join(" ", new ArrayList<>(intersection));

		System.out.println(result);

		return result;
	}

	public static Map<String, Double> borda(ArrayList<String> termLists,int z) {

		ArrayList<List<String>> allLists = new ArrayList<List<String>>();
		HashMap<String, Double> finalScore = new HashMap<>();

		for(String list: termLists) {
			allLists.add(Arrays.asList(list.split(" ")));
		}
		
		// Add all terms to hashset
		HashSet<String> allTerms = new HashSet<>();
		for(List<String> list: allLists) {
			allTerms.addAll(list);
		}
		
		// Calculate brodacount for each term
		ArrayList<String> finalTermsList = new ArrayList<>(allTerms);
		
		for(String key:finalTermsList) {
			int score = 0;
			final int MAX_LIST_SIZE = BiomedQA.TOP_TERMS_TO_SELECT[z];
			
			for(List<String> list: allLists) {
				if(list.contains(key)) score += MAX_LIST_SIZE - list.indexOf(key);
			}
			
			finalScore.put(key, (double)score);
		}
		
		
		return CollectionHelper.sortByComparator(finalScore, false);
	}

	public static Map<String, Double> linear(ArrayList<Map<String, Double>> termLists, double alpha) {

		// Assuming only two techniques are being combined linearly
		Map<String, Double> termList1 = termLists.get(0);
		Map<String, Double> termList2 = termLists.get(1);

		// normalize both lists - assuming that the first element is has the highest score
		termList1 = CollectionHelper.normalizeScore(termList1);
		termList2 = CollectionHelper.normalizeScore(termList2);

		// now combine scores using linear combination
		Map<String, Double> finalList = new HashMap<String, Double>();

		finalList.putAll(termList1);

		for(Map.Entry<String, Double> entry : termList2.entrySet()) {

			double firstScore = 0.0;
			double secondScore = entry.getValue();
			String key = entry.getKey();

			if(termList1.containsKey(key)) firstScore = termList1.get(key);

			double finalScore = alpha * firstScore + (1-alpha) * secondScore;
			finalList.put(key, finalScore);
		}

		return CollectionHelper.sortByComparator(finalList, false);
	}
}

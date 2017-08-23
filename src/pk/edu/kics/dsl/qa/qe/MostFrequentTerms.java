package pk.edu.kics.dsl.qa.qe;

import java.util.Map;
import java.util.TreeMap;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;
import pk.edu.kics.dsl.qa.util.ValueComparator;

public class MostFrequentTerms extends LocalQueryExpansion {

	@Override
	public String getRelevantTerms(Question question, int termsToSelect) {
		try {
			super.init(question);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Map<String,Integer> terms = CollectionHelper.sortByComparatorInt(relevantTermsTotalFrequency, false); 
		return CollectionHelper.getTopTerms(terms, termsToSelect);
	}

	
	
	/*private ArrayList<String> getOrderedTermsByFrequency(ArrayList<String> allContentWords) {

		Map<Object, Long> occurrences = allContentWords.stream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		Map sortedMap = sortByValue(occurrences);
		// sortedMap.forEach((k,v) -> System.out.println("key: "+k+" value:"+v));
		int i = 1;
		ArrayList<String> keyList = new ArrayList<>();
		for (Object keyword : sortedMap.entrySet()) {
			String value = keyword.toString().substring(0, keyword.toString().indexOf("=")) + " ";
			if (i > NUMBER_OF_TERMS)
				break;
			else
				keyList.add(value);
			i++;
		}

		return keyList;
	}*/

	

}

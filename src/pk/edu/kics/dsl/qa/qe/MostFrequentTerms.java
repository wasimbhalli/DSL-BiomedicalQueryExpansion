package pk.edu.kics.dsl.qa.qe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.ValueComparator;

public class MostFrequentTerms extends LocalQueryExpansion {

	ArrayList<String> allContentWords = new ArrayList<String>();
	final int NUMBER_OF_TERMS = 10;

	@Override
	public String getRelevantTerms(Question question) {

		// Call to server and populate resultsList and content words method
		try {
			super.setResultsList(question);
			super.SetAllContentWords();
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}

		return StringUtils.join(getOrderedTermsByFrequency(allContentWords), " ");
	}

	private ArrayList<String> getOrderedTermsByFrequency(ArrayList<String> allContentWords) {

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
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map sortByValue(Map unsortedMap) {
		Map sortedMap = new TreeMap(new ValueComparator(unsortedMap));
		sortedMap.putAll(unsortedMap);
		return sortedMap;
	}

}

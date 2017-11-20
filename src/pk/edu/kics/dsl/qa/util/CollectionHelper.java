package pk.edu.kics.dsl.qa.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.util.Precision;

import pk.edu.kics.dsl.qa.BiomedQA;

public class CollectionHelper {

	public static Map<String, Double> sortByComparator(Map<String, Double> unsortMap, final boolean order)
	{
		List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(unsortMap.entrySet());
		Collections.sort(list, new Comparator<Entry<String, Double>>()
		{
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2)
			{
				if (order)
				{
					return o1.getValue().compareTo(o2.getValue());
				}
				else
				{
					return o2.getValue().compareTo(o1.getValue());

				}
			}
		});

		// Maintaining insertion order with the help of LinkedList
		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Entry<String, Double> entry : list)
		{
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	public static Map<String, Double> sortByComparatorInt(Map<String, Integer> unsortMap, final boolean order)
	{
		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());
		Collections.sort(list, new Comparator<Entry<String, Integer>>()
		{
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2)
			{
				if (order)
				{
					return o1.getValue().compareTo(o2.getValue());
				}
				else
				{
					return o2.getValue().compareTo(o1.getValue());

				}
			}
		});

		// Maintaining insertion order with the help of LinkedList
		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Entry<String, Integer> entry : list)
		{
			sortedMap.put(entry.getKey(), (double) entry.getValue());
		}

		return sortedMap;
	}

	public static <T,U> String getTopTerms(Map<T,U> terms, int termsToSelect) {

		StringBuilder sb = new StringBuilder();
		StringBuilder sb_debug = new StringBuilder();

		int counter = 1;
		for (Map.Entry<T,U> entry : terms.entrySet()) {
			String key = (String) entry.getKey();
			sb.append(key).append(" ");
			
			if(BiomedQA.DISPLAY_RESULTS) {
				sb_debug.append(key).append("(").append(Precision.round(Double.parseDouble(entry.getValue().toString()), 2)).append(") | ");
			}
			if(counter++>=termsToSelect) break;
		}

		if(BiomedQA.DISPLAY_RESULTS) {
			System.out.println(sb_debug.toString());
		}

		return sb.toString();
	}
	
	public static Map<String, Double> normalizeScore(Map<String, Double> deNormalizedMap)
	{
		Map<String, Double> normalizedMap = new LinkedHashMap<String, Double>();
		double highest_score = 0.0;

		Map.Entry<String,Double> firstEntry = deNormalizedMap.entrySet().iterator().next();
		highest_score = firstEntry.getValue();
		
		for (Map.Entry<String, Double> entry : deNormalizedMap.entrySet()) {
			normalizedMap.put(entry.getKey(), entry.getValue()/highest_score);
		}
		
		return normalizedMap;
	}

}

package pk.edu.kics.dsl.qa.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeFactory;

public class StringHelper {

	public static ArrayList<String> analyzeContent(String content) throws IOException {
		AttributeFactory factory = AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY;
		StandardTokenizer tokenizer = new StandardTokenizer(factory);
		ArrayList<String> tokens = new ArrayList<String>();
		tokenizer.setReader(new StringReader(content));
		tokenizer.reset();
		CharTermAttribute attr = tokenizer.addAttribute(CharTermAttribute.class);

		Map<String, String> param = new HashMap<>();
		param.put("luceneMatchVersion", "LUCENE_66");
		
		LowerCaseFilterFactory lowerCaseFactory = new LowerCaseFilterFactory(param);
		TokenStream tokenStream = lowerCaseFactory.create(tokenizer);
		
		//StopFilterFactory stopFilterFactory = new StopFilterFactory(param);
		//TokenStream stopWordRemoveStream = stopFilterFactory.create(tokenStream);

		while(tokenStream.incrementToken()) {
			String term = attr.toString();
			tokens.add(term);
		}

		tokenizer.close();
		return tokens;
	}
	
	public static Map<String , Integer> getWordsFrequency(ArrayList<String> tokens) {
		Map<String , Integer> dictionary=new HashMap<String,Integer>();
		for(String s:tokens){
			s = s.replaceAll(",", "");
			if(dictionary.containsKey(s))
				dictionary.put(s, dictionary.get(s)+1);
			else
				dictionary.put(s, 1);
		}

		return dictionary;
	}

	public static String getTermsByComma(ArrayList<String> terms) {
		return String.join(",", terms);
	}
}

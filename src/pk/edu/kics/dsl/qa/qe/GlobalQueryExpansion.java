package pk.edu.kics.dsl.qa.qe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.parser.ParseException;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.services.MetaMapServiceCall;
import pk.edu.kics.dsl.qa.util.MetaMapResults;

public class GlobalQueryExpansion extends QueryExpansion {

	@Override
	public String getRelevantTerms(Question question) {
		// TODO Auto-generated method stub
		return null;
	}

	public void getMetamapSynonyms(String question) {
		MetaMapServiceCall metaMapServiceCall = new MetaMapServiceCall();
		MetaMapResults resultParser;
		try {
			resultParser = new MetaMapResults(metaMapServiceCall.getSimilarWords(question));
			HashMap<String, Object> resultsList = resultParser.getMetaMapResult();
			ArrayList<String> similarWordQuestion = resultParser.getKeywords(resultsList);
			
			for (String words : similarWordQuestion) {
				System.out.println("Word :"+words.toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

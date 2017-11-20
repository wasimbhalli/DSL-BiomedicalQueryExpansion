package pk.edu.kics.dsl.qa.qe;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.services.MetaMapServiceCall;
import pk.edu.kics.dsl.qa.util.HttpHelper;
import pk.edu.kics.dsl.qa.util.MetaMapResults;

public class GlobalQueryExpansion extends QueryExpansion {

	@Override
	public Map<String, Double> getRelevantTerms(Question question) { return null; }

	public String getRelatedTerms(Question question) {
		return getMetamapVariants(question.getQuestion());
	}

	public String getMetamapVariants(String question) {

		MetaMapServiceCall metaMapServiceCall = new MetaMapServiceCall();
		String response = metaMapServiceCall.getSimilarWords(question);

		LinkedHashMap<String, List<String>> resultMetamap = MetaMapResults.ParseMetamapVariantsReponse(response);

		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, List<String>> entry : resultMetamap.entrySet()) {

			String key = entry.getKey();
			List<String> variants = entry.getValue();

			for(String variantTerm: variants) {
				sb.append(variantTerm).append(" ");
			}
		}

		return sb.toString();
	}

	public ArrayList<String> getMetamapSynonyms(String question) {
		MetaMapServiceCall metaMapServiceCall = new MetaMapServiceCall();
		MetaMapResults resultParser;
		ArrayList<String> similarKeywords = new ArrayList<>();

		try {
			resultParser = new MetaMapResults(metaMapServiceCall.getSimilarWords(question));
			HashMap<String, Object> resultsList = resultParser.getMetaMapResult();
			similarKeywords = resultParser.getKeywords(resultsList);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return similarKeywords;
	}


	public static String getTranslatedQuery(String question) {

		String serviceURL = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=mesh&term="
				+ URLEncoder.encode(question) + "&sort=relevance&retmax=1";
		String translatedQuery = "";

		try {
			String response = HttpHelper.getResponse(serviceURL, "");
			translatedQuery = parseTranslatedQuery(response);

		} catch (IOException e) {
			e.printStackTrace();
		}

		translatedQuery = translatedQuery.replaceAll("\\[[^]]*\\]", "").replace("?", "");
		
		return translatedQuery;
	}

	public static String parseTranslatedQuery(String XmlString)
	{

		String xpath = "/eSearchResult/QueryTranslation";
		XPath xPath = XPathFactory.newInstance().newXPath();
		String translatedQ = "";

		try {
			translatedQ = xPath.evaluate(xpath, new InputSource(new StringReader(XmlString)));
		} catch (XPathExpressionException e1) {
			e1.printStackTrace();
		}

		return translatedQ;
	}


	/*public ArrayList<String> getWordnetSynonyms(String question)
	{

		ArrayList<String> finalSynonymsList = null;
		//  Get the synsets containing the word form=capicity

		File f=new File("data/WordNet-3.0/dict");
		System.setProperty("wordnet.database.dir", f.toString());
		//setting path for the WordNet Directory

		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] synsets = database.getSynsets(question);
		//  Display the word forms and definitions for synsets retrieved

		if (synsets.length > 0){
			finalSynonymsList = new ArrayList<String>();
			// add elements to al, including duplicates
			HashSet synonymsHashlist = new HashSet();
			for (int i = 0; i < synsets.length; i++){
				String[] wordForms = synsets[i].getWordForms();
				for (int j = 0; j < wordForms.length; j++)
				{
					finalSynonymsList.add(wordForms[j]);
				}


				//removing duplicates
				synonymsHashlist.addAll(finalSynonymsList);
				finalSynonymsList.clear();
				finalSynonymsList.addAll(synonymsHashlist);

			}
		}
		return finalSynonymsList;
	}*/
}

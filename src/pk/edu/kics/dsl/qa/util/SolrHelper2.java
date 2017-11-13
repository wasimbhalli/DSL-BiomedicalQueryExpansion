package pk.edu.kics.dsl.qa.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.entity.SolrResult;

public class SolrHelper2 {

	static String urlString = "http://"  + BiomedQA.SOLR_SERVER + ":8983/solr/" + BiomedQA.SOLR_CORE;
	static SolrClient solr;
	static SolrQuery solrQuery;

	public SolrHelper2() {
		solr = new HttpSolrClient.Builder(urlString).build();
		solrQuery = new SolrQuery();
	}

	public ArrayList<SolrResult> submitQuery(Question question, int start, int rowNo)
			throws SolrServerException, IOException {

		solrQuery.setQuery(question.text);
		solrQuery.setRequestHandler("/select");
		solrQuery.setStart(start);
		solrQuery.setRows(rowNo);
		solrQuery.set("fl", "", "score", "id","body");

		QueryResponse response = solr.query(solrQuery);
		SolrDocumentList documentList = response.getResults();
		ArrayList<SolrResult> resultsList = new ArrayList<>();
		int rank = 1;

		for (SolrDocument document : documentList) {

			SolrResult solrResult = new SolrResult();

			solrResult.setTopicId(String.valueOf(question.topicId));
			String pmid = document.get("id").toString();
			solrResult.setPmid(pmid.substring(pmid.lastIndexOf("/") + 1, pmid.lastIndexOf(".")));
			solrResult.setRank(rank++);
			solrResult.setStartOffset(0);
			solrResult.setLength(0);
			solrResult.setScore((float)document.get("score"));
			solrResult.setContent(document.get("body").toString());

			resultsList.add(solrResult);
		}

		return resultsList;
	}

	public ArrayList<HashMap<String, Integer>> getCorpusStatistics(String terms) throws IOException, ParseException, JSONException {

		String urlParameters  = "terms.list=" + terms;
		String url = urlString + "/terms?wt=json&terms.fl=body&terms.ttf=true";
		String response = HttpHelper.getResponse(url, urlParameters);

		JSONObject jsonObject = new JSONObject(response);
		JSONObject termsObject = (JSONObject) jsonObject.get("terms");
		JSONObject body =  (JSONObject) termsObject.get("body");

		return getTTFDFDictionary(body);
	}

	public static ArrayList<HashMap<String, Integer>> getTTFDFDictionary(JSONObject body) throws JSONException {
		
		HashMap<String, Integer> totalTermFrequency = new HashMap<>();
		HashMap<String, Integer> documentFrequency = new HashMap<>();
		ArrayList<HashMap<String, Integer>> statistics = new ArrayList<>();
		
		Iterator<?> keys = body.keys();

		while( keys.hasNext() ) {
			String key = (String)keys.next();
			JSONObject df_ttf = (JSONObject) body.get(key);
			documentFrequency.put(key, Integer.parseInt(df_ttf.get("df").toString()));
			totalTermFrequency.put(key, Integer.parseInt(df_ttf.get("ttf").toString()));
		}
		
		statistics.add(totalTermFrequency);
		statistics.add(documentFrequency);
		
		return statistics;
	}

	/*public static HashMap<String, Integer> getCorpusTermsFrequency(ArrayList<String> terms) throws IOException, ParseException, JSONException, SolrServerException {

		HashMap<String, Integer> termsFrequency = new HashMap<>();

		ArrayList<String> tempTermsList = new ArrayList<>();
		for (int i = 0; i < terms.size(); i++) {
			tempTermsList.add("ttf(" + BiomedQA.CONTENT_FIELD + "," + terms.get(i) + ")");

			// SOLR not accepting all terms together - so sending chunks of 4000
			if(tempTermsList.size() == 20) {
				String concatenateTerms = String.join(",", tempTermsList);
				termsFrequency.putAll(getCorpusTermsFrequencySubset(concatenateTerms));
				System.out.println(concatenateTerms);
				tempTermsList.clear();
			}
		}

		if(tempTermsList.size()>0) {
			String concatenateTerms = String.join(",", tempTermsList);
			termsFrequency.putAll(getCorpusTermsFrequencySubset(concatenateTerms));
			tempTermsList.clear();
		}

		return termsFrequency;
	}

	private static HashMap<String, Integer> getCorpusTermsFrequencySubset(String terms) throws IOException, JSONException {
		String urlParameters  = "fl=" + terms;
		String url = urlString + "/select?q=*:*&rows=1&wt=json&indent=true";
		String response = HttpHelper.getResponse(url, urlParameters);

		JSONObject jsonObject = new JSONObject(response);
		JSONObject jsonResponse = (JSONObject) jsonObject.get("response");
		JSONObject docs =  (JSONObject)(((JSONArray)jsonResponse.get("docs")).get(0));

		return parseCorpusTermFrequency(docs);
	}*/

	public static HashMap<String, Integer> parseCorpusTermFrequency(JSONObject docs) throws JSONException {
		HashMap<String, Integer> allTerms = new HashMap<>();
		Iterator<?> keys = docs.keys();

		while( keys.hasNext() ) {
			String fieldKey = (String)keys.next();

			String key = fieldKey.split(",")[1];
			key = key.substring(0, key.length() - 1);

			allTerms.put(key, (int)docs.get(fieldKey));
		}

		return allTerms;
	}

	public static long getCorpusTermsFrquencySum() throws IOException, JSONException {
		String url = urlString + "/select?q=*:*&rows=1&wt=json&indent=true&fl=sttf(body)";
		String response = HttpHelper.getResponse(url,"");

		JSONObject jsonObject = new JSONObject(response);
		JSONObject jsonResponse = (JSONObject) jsonObject.get("response");
		JSONObject docs =  (JSONObject)(((JSONArray)jsonResponse.get("docs")).get(0));

		return parseCorpusTermTotalFrequency(docs);
	}

	public static long parseCorpusTermTotalFrequency(JSONObject docs) throws JSONException {

		long totalTermFrequency = 0;
		Iterator<?> keys = docs.keys();

		while( keys.hasNext() ) {
			String fieldKey = (String)keys.next();
			String tempValue = String.valueOf(docs.get(fieldKey));
			totalTermFrequency = Long.parseLong(tempValue);
		}

		return totalTermFrequency;
	}

	/*public int getFalsePositive(String term)
			throws SolrServerException, IOException, NumberFormatException, JSONException {
		// TODO: What to do with : as colon is used as field separator.
		solrQuery.setQuery(term.replaceAll(":", ""));
		solrQuery.setRequestHandler("/select");
		solrQuery.setRows(0);

		QueryResponse response = solr.query(solrQuery);
		SolrDocumentList documentList = response.getResults();
		JSONObject jsonObject = new JSONObject(documentList);
		int numFound = Integer.parseInt(jsonObject.get("numFound").toString());

		return BiomedQA.TOTAL_DOCUMENTS - numFound;
	}TO BE REMOVED */

}
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
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.entity.SolrResult;

public class SolrHelper {

	static String urlString = "http://localhost:8983/solr/genomic_html";
	SolrClient solr;
	SolrQuery solrQuery;

	public SolrHelper() {
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

	public static HashMap<String, Integer> getTermDocumentFrequency(String terms) throws IOException, ParseException, JSONException {

		String urlParameters  = "terms.list=" + terms;
		String url = urlString + "/terms?wt=json&terms.fl=body";
		String response = HttpHelper.getResponse(url, urlParameters);

		JSONObject jsonObject = new JSONObject(response);
		JSONObject termsObject = (JSONObject) jsonObject.get("terms");
		JSONObject body =  (JSONObject) termsObject.get("body");

		return getDFDictionary(body);
	}
	
	public static HashMap<String, Integer> getDFDictionary(JSONObject body) throws JSONException {
		HashMap<String, Integer> allTerms = new HashMap<>();
		Iterator<?> keys = body.keys();

		while( keys.hasNext() ) {
			String key = (String)keys.next();
			allTerms.put(key, (int)body.get(key));
		}

		return allTerms;
	}
}

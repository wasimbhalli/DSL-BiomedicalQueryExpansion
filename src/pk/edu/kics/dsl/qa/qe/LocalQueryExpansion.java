package pk.edu.kics.dsl.qa.qe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONException;
import org.json.simple.parser.ParseException;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.entity.SolrResult;
import pk.edu.kics.dsl.qa.util.SolrHelper;
import pk.edu.kics.dsl.qa.util.StringHelper;

public class LocalQueryExpansion extends QueryExpansion{

	private SolrHelper solrHelper;
	protected ArrayList<SolrResult> resultsList;
	protected ArrayList<String> dictionary = new ArrayList<>();
	protected static HashMap<String, Integer> relevantTermsTotalFrequency = new HashMap<>();
	protected static HashMap<String, Integer> documentFrequency = new HashMap<>();
	protected Map<Integer, Map<String, Integer>> documentTermFrequencies = new HashMap<Integer, Map<String, Integer>>();
	protected RealMatrix tdMatrix;

	public void init(Question question) throws SolrServerException, IOException, ParseException, JSONException {
		setSolrDocuments(question);
		buildTermDocumentMatrix(resultsList);
		String terms = StringHelper.getTermsByComma(dictionary);
		documentFrequency = SolrHelper.getTermDocumentFrequency(terms);
	}
	
	@Override
	public String getRelevantTerms(Question question, int termsToSelect) {
		return null;
	}
	
	public ArrayList<SolrResult> getResultsList() {
		return resultsList;
	}

	public void setSolrDocuments(Question q) throws SolrServerException, IOException {
		this.resultsList = solrHelper.submitQuery(q, 0, BiomedQA.DOCUMENTS_FOR_QE);
	}

	public LocalQueryExpansion() {
		this.solrHelper = new SolrHelper();

	}
	
	protected void buildTermDocumentMatrix(ArrayList<SolrResult> resultsList) throws IOException {

		// Important: Normalize similarly both in getWordsFrequency and individual token while adding to dictionary
		for (int docCounter = 0; docCounter < resultsList.size(); docCounter++) {
			SolrResult result = resultsList.get(docCounter);
			ArrayList<String> tokens = StringHelper.analyzeContent(result.getContent());
			documentTermFrequencies.put(docCounter, StringHelper.getWordsFrequency(tokens));

			for (int i = 0; i < tokens.size(); i++) {
				String currentTerm = tokens.get(i).replaceAll(",", ""); //Fix: 1000,00 to 100000
				if(!dictionary.contains(currentTerm)) {
					dictionary.add(currentTerm);
					relevantTermsTotalFrequency.put(currentTerm, 0);
				}
			}
		}

		tdMatrix = new Array2DRowRealMatrix(dictionary.size(), BiomedQA.DOCUMENTS_FOR_QE);

		for(int docIndex=0; docIndex<BiomedQA.DOCUMENTS_FOR_QE; docIndex++) {
			HashMap<String,Integer> TermsFrequency = (HashMap<String,Integer>) documentTermFrequencies.get(docIndex);

			for (String key : TermsFrequency.keySet()) {
				int row = ((ArrayList<String>) dictionary).indexOf(key);
				int newCount = relevantTermsTotalFrequency.get(key) + TermsFrequency.get(key);
				relevantTermsTotalFrequency.put(key, newCount); 
				tdMatrix.setEntry(row, docIndex, TermsFrequency.get(key));
			}
		}
	}

}

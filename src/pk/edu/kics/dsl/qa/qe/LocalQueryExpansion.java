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
import pk.edu.kics.dsl.qa.util.SolrHelper2;
//import pk.edu.kics.dsl.qa.util.SolrHelper;
//import pk.edu.kics.dsl.qa.util.SolrHelper2;
//import pk.edu.kics.dsl.qa.util.SolrHelper;
import pk.edu.kics.dsl.qa.util.StringHelper;

public class LocalQueryExpansion extends QueryExpansion{

	private SolrHelper2 solrHelper;
	//private SolrHelper solrHelper;
	protected ArrayList<SolrResult> resultsList;
	
	protected ArrayList<String> localDictionary = new ArrayList<>();
	
	protected HashMap<String, Integer> localTermsTotalFrequency = new HashMap<>();
	protected HashMap<String, Integer> termsTotalFrequency = new HashMap<>();
	
	protected HashMap<Integer, Integer> documentTotalTerms = new HashMap<>();
	
	// True Positive: Number of local documents in which term is present. False Negative: In which term is absent
	protected HashMap<String, Integer> localDocumentFrequency = new HashMap<>();
	
	// False Positive: Number of documents in which term is present. True Negative: In which term is absent
	protected HashMap<String, Integer> documentFrequency = new HashMap<>();
	
	// Frequency of each term appeared in each document
	protected Map<Integer, Map<String, Integer>> documentTermFrequencies = new HashMap<Integer, Map<String, Integer>>();
	
	// Term-Document Matrix
	protected RealMatrix tdMatrix;
	
	protected long totalCorpusTermsFrquency = 0;

	public void init(Question question,int docCount) throws SolrServerException, IOException, ParseException, JSONException {
		setSolrDocuments(question,docCount);
		buildTermDocumentMatrix(resultsList);
		String terms = StringHelper.getTermsByComma(localDictionary);
		ArrayList<HashMap<String, Integer>> statistics = solrHelper.getCorpusStatistics(terms);
		termsTotalFrequency = statistics.get(0);
		documentFrequency = statistics.get(1);
		totalCorpusTermsFrquency = SolrHelper2.getCorpusTermsFrquencySum();
		//totalCorpusTermsFrquency = SolrHelper.getCorpusTermsFrquencySum();
		
		for(String term: localDictionary) {			
			for (int i = 0; i < resultsList.size(); i++) {
				HashMap<String,Integer> TermsFrequency = (HashMap<String,Integer>) documentTermFrequencies.get(i);
				if(TermsFrequency.containsKey(term)) {
					if(localDocumentFrequency.containsKey(term)) {
						localDocumentFrequency.put(term, localDocumentFrequency.get(term) + 1);
					} else {
						localDocumentFrequency.put(term, 1);
					}
				}
			}
		}
	}
	
	@Override
	public Map<String, Double> getRelevantTerms(Question question,int docCount) {
		return null;
	}
	
	public ArrayList<SolrResult> getResultsList() {
		return resultsList;
	}

	public void setSolrDocuments(Question q,int docCount) throws SolrServerException, IOException {
		this.resultsList = solrHelper.submitQuery(q, 0, BiomedQA.DOCUMENTS_FOR_QE[docCount]);
	}

	public LocalQueryExpansion() {
		this.solrHelper = new SolrHelper2();
		//this.solrHelper = new SolrHelper();

	}
	
	protected void buildTermDocumentMatrix(ArrayList<SolrResult> resultsList) throws IOException {

		// Important: Normalize similarly both in getWordsFrequency and individual token while adding to dictionary
		for (int docCounter = 0; docCounter < resultsList.size(); docCounter++) {
			SolrResult result = resultsList.get(docCounter);
			ArrayList<String> tokens = StringHelper.analyzeContent(result.getContent(), false);
			documentTermFrequencies.put(docCounter, StringHelper.getWordsFrequency(tokens));
			documentTotalTerms.put(docCounter, tokens.size());
			for (int i = 0; i < tokens.size(); i++) {
				String currentTerm = StringHelper.normalizeWord(tokens.get(i));
				if(!localDictionary.contains(currentTerm)) {
					localDictionary.add(currentTerm);
					localTermsTotalFrequency.put(currentTerm, 0);
				}
			}
		}

		tdMatrix = new Array2DRowRealMatrix(localDictionary.size(), resultsList.size());

		for(int docIndex=0; docIndex<resultsList.size(); docIndex++) {
			HashMap<String,Integer> TermsFrequency = (HashMap<String,Integer>) documentTermFrequencies.get(docIndex);

			for (String key : TermsFrequency.keySet()) {
				int row = ((ArrayList<String>) localDictionary).indexOf(key);
				int newCount = localTermsTotalFrequency.get(key) + TermsFrequency.get(key);
				localTermsTotalFrequency.put(key, newCount); 
				tdMatrix.setEntry(row, docIndex, TermsFrequency.get(key));
			}
		}
	}
	
}

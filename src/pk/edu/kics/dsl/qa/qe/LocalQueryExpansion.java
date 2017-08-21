package pk.edu.kics.dsl.qa.qe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.solr.client.solrj.SolrServerException;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.entity.SolrResult;
import pk.edu.kics.dsl.qa.util.SolrHelper;
import pk.edu.kics.dsl.qa.util.StringUtil;

public class LocalQueryExpansion extends QueryExpansion{

	private SolrHelper solrHelper;
	protected ArrayList<SolrResult> resultsList;
	protected ArrayList<String> allContentWords;
	
	public ArrayList<SolrResult> getResultsList() {
		return resultsList;
	}

	public void setResultsList(Question q) throws SolrServerException, IOException {
		this.resultsList = solrHelper.submitQuery(q, 0, 10);
	}
	
	public void SetAllContentWords() {
		allContentWords = new ArrayList<String>();
		for (Iterator iterator = resultsList.iterator(); iterator.hasNext();) {
			SolrResult solrResult = (SolrResult) iterator.next();
			ArrayList<String> contentWords = StringUtil.deleteStopWord(solrResult);
			
			allContentWords.addAll(contentWords);
		}
	}

	public LocalQueryExpansion() {
		this.solrHelper = new SolrHelper();
		 
	}

	@Override
	public String getRelevantTerms(Question question) {
		return null;
	}
	

}

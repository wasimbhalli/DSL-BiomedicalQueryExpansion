package pk.edu.kics.dsl.qa;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONException;
import org.json.simple.parser.ParseException;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.entity.SolrResult;
import pk.edu.kics.dsl.qa.qe.QueryExpansion;
import pk.edu.kics.dsl.qa.util.Evaluation;
import pk.edu.kics.dsl.qa.util.IOHelper;
import pk.edu.kics.dsl.qa.util.SolrHelper;

public class BiomedQA {

	public final static boolean DISPLAY_RESULTS = true;
	public final static int DOCUMENTS_FOR_QE = 10;
	public final static int TOTAL_DOCUMENTS = 162259;
	public final static String CONTENT_FIELD = "body";	
	
	private final static int TOP_TERMS_TO_SELECT = 10;
	private final static String EXPERIMENT = "TFIDF";
	private final static String QUESTIONS_PATH = "resources/2007topics.txt";
	
	public static long totalCorpusTermsFrquency = 0;

	public static void main(String[] args) throws IOException, SolrServerException, ParseException, JSONException {
		
		ArrayList<Question> questionsList = IOHelper.ReadQuestions(QUESTIONS_PATH);
		String qeClass = "pk.edu.kics.dsl.qa.qe." + EXPERIMENT;
		SolrHelper solrHelper = new SolrHelper();
	
		try {
			int counter = 1;
			QueryExpansion qe = (QueryExpansion) Class.forName(qeClass).newInstance();
			totalCorpusTermsFrquency = SolrHelper.getCorpusTermsFrquencySum();

			for (Question question : questionsList) {
				String relevantTerms = qe.getRelevantTerms(question, TOP_TERMS_TO_SELECT);
				question.setQuestion(qe.mergeTerms(question.getQuestion(), relevantTerms));
				ArrayList<SolrResult> resultsList = solrHelper.submitQuery(question, 0, 1000);
				IOHelper.writeResult(resultsList, counter++);	
			}
		}	
		catch (Exception e) {
			e.printStackTrace();
		}
		
		Evaluation.evaluateResults(EXPERIMENT);
	}
}
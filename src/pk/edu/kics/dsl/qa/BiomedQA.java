package pk.edu.kics.dsl.qa;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONException;
import org.json.simple.parser.ParseException;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.entity.SolrResult;
import pk.edu.kics.dsl.qa.qe.*;
import pk.edu.kics.dsl.qa.util.Evaluation;
import pk.edu.kics.dsl.qa.util.IOHelper;
import pk.edu.kics.dsl.qa.util.SolrHelper;
import pk.edu.kics.dsl.qa.util.StringHelper;

public class BiomedQA {

	// If no technique is to be used, use "NQ" as QE_TECHNIQUE which means no Query Expansion
	private final static String[] QE_TECHNIQUES = {"WE"};
	

	//private final static String[] QE_TECHNIQUES = {"NQ"};
	// IR_MODEL also needs to be changed in core's managed-scheme file to work.
	//private final static String IR_MODEL = ""; 

	public final static int DOCUMENTS_FOR_QE = 10;
	private final static int TOP_TERMS_TO_SELECT = 10;
	public final static boolean DISPLAY_RESULTS = false;

	private final static String QUESTIONS_PATH = "resources/2007topics.txt";
	public final static String SOLR_SERVER = "localhost";
	public final static String SOLR_CORE = "genomic_html";
	public final static String CONTENT_FIELD = "body";	
	public final static int TOTAL_DOCUMENTS = 162259;


	public static void main(String[] args) throws IOException, SolrServerException, ParseException, JSONException {

		ArrayList<Question> questionsList = IOHelper.ReadQuestions(QUESTIONS_PATH);
		
		try {
			String experiment = "";
			
			for (int i = 0; i < QE_TECHNIQUES.length; i++) {
				experiment = QE_TECHNIQUES[i];// + "-" + IR_MODEL;
				IOHelper.deletePreviousResults();
				processAllQuestions(questionsList, QE_TECHNIQUES[i]);
				Evaluation.evaluateResults(experiment);

				System.out.println("Done: " + experiment);
			}

		}	
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void processAllQuestions(ArrayList<Question> questionsList, String qeTechnique) throws Exception {
		int counter = 1;
		for (Question question : questionsList) {
			processQuestion(question, qeTechnique, counter++);
		}
	}
	
	private static void processQuestion(Question question, String qeTechnique, int counter) throws Exception {
		
		SolrHelper solrHelper = new SolrHelper();
		Question processedQ = new Question();
		String relevantTerms = "";
		String queryWords = question.getQuestion();
		
		if(!qeTechnique.toLowerCase().equals("nq")) {
			String qeClass = "pk.edu.kics.dsl.qa.qe." + qeTechnique;
			QueryExpansion qe = (QueryExpansion) Class.forName(qeClass).newInstance();
			relevantTerms = qe.getRelevantTerms(question, TOP_TERMS_TO_SELECT);
			queryWords = qe.mergeTerms(queryWords, relevantTerms);
		}

		ArrayList<String> finalQuery = StringHelper.solrPreprocessor(queryWords);

		processedQ.setTopicId(question.topicId);
		processedQ.setQuestion(String.join(" ", finalQuery));

		// There are a maximum of ~614 documents for any particular topic
		ArrayList<SolrResult> resultsList = solrHelper.submitQuery(processedQ, 0, 650);
		IOHelper.writeResult(resultsList, counter);
	}
	
	
}
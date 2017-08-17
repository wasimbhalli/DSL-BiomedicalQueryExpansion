package pk.edu.kics.dsl.qa;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.solr.client.solrj.SolrServerException;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.entity.SolrResult;
import pk.edu.kics.dsl.qa.qe.MostFrequentTerms;
import pk.edu.kics.dsl.qa.qe.QueryExpansion;
import pk.edu.kics.dsl.qa.util.Evaluation;
import pk.edu.kics.dsl.qa.util.IOHelper;
import pk.edu.kics.dsl.qa.util.SolrHelper;

// Set the Core Name in SolrHelper (specific to the core you have in your Solr installation)

public class BiomedQA {

	final static String experimentClass = "MostFrequentTerms";
	final static String questionPath = "resources/2007topics.txt";

	public static void main(String[] args) throws IOException, SolrServerException {
		SolrHelper solrHelper = new SolrHelper();
		ArrayList<Question> questionsList = IOHelper.ReadQuestions(questionPath);

		for (Question question : questionsList) {
			
			String qeClass = "pk.edu.kics.dsl.qa.qe." + experimentClass;
			try {

				QueryExpansion qe = (QueryExpansion) Class.forName(qeClass).newInstance();
				String relevantTerms = qe.getRelevantTerms(question);
				question.setQuestion(qe.mergeTerms(question.getQuestion(), relevantTerms));
				ArrayList<SolrResult> resultsList = solrHelper.submitQuery(question, 0, 1000);
				IOHelper.writeResult(resultsList);				
				
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Evaluation.evaluateResults(experimentClass);
	}

}

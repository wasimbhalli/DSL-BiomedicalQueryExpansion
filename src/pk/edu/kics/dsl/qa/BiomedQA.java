package pk.edu.kics.dsl.qa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONException;
import org.json.simple.parser.ParseException;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.entity.SolrResult;
import pk.edu.kics.dsl.qa.qe.GlobalQueryExpansion;
import pk.edu.kics.dsl.qa.qe.QueryExpansion;
import pk.edu.kics.dsl.qa.util.CollectionHelper;
import pk.edu.kics.dsl.qa.util.CombHelper;
import pk.edu.kics.dsl.qa.util.Evaluation;
import pk.edu.kics.dsl.qa.util.IOHelper;
import pk.edu.kics.dsl.qa.util.SimilarityHelper;
import pk.edu.kics.dsl.qa.util.SolrHelper;
import pk.edu.kics.dsl.qa.util.StringHelper;

public class BiomedQA {

	public static enum EvaluationType
	{
		BIOAsq,
		TRECGenomic
	};
	private enum Combination {
		Intersection, 
		Borda, 
		Linear // Only two techniques will be used for this combination
	};

	public static enum SemanticSource {
		MeSH, 
		WordEmbedding
	};

	private final static Boolean COMBINATION_ENABLED = false;
	private final static Combination COMBINATION_TECHNIQUE = Combination.Linear;
	private final static double LINEAR_ALPHA = 0.6;

	// If no technique is to be used, use "Baseline" as QE_TECHNIQUE which means no Query Expansion
	private final static String[] QE_TECHNIQUES = {"Baseline", "ACC2", "BNS", "Bose", "ChiSquare", "CoCosine", 
			"CoDice", "CoJaccard", "IdeRegular","MFT", "IDF", "TFIDF", "IG", "KLDivergence", "LRF", 
			"OddsRatio", "PRF", "Rocchio", "RSV"};
	public final static int DOCUMENTS_FOR_QE = 5;
	public final static int TOP_TERMS_TO_SELECT = 5;
	public final static boolean DISPLAY_RESULTS = true;

	public final static boolean STEMMING_ENABLED = false;

	public final static boolean GLOBAL_QE_ENABLED = false;

	// only applicable for individual feature selection technique - not for combinations
	public final static SemanticSource SEMANTIC_SOURCE_TECHNIQUE = SemanticSource.MeSH;
	public final static boolean SEMANTIC_FILTERING_ENABLED = false;
	public final static int TOP_TERMS_FOR_SEMANTIC_FILTERING = 5;

	private final static String QUESTIONS_PATH = "resources/BioASQ-task5bPhaseA-testset1";
	public final static String SOLR_SERVER = "localhost";
	public final static String SOLR_CORE = "trec_genomic";
	public final static String CONTENT_FIELD = "body";	
	public final static int TOTAL_DOCUMENTS = 162259;
	public final static EvaluationType evalType = EvaluationType.BIOAsq;
	public static int TOTAL_QUESTION;

	public static void main(String[] args) throws IOException, SolrServerException, ParseException, JSONException {

		ArrayList<Question> questionsList = IOHelper.ReadQuestions(QUESTIONS_PATH,evalType);
		TOTAL_QUESTION = questionsList.size();
		try {
			String experiment = "";

			if(!COMBINATION_ENABLED) {
				for (int i = 0; i < QE_TECHNIQUES.length; i++) {
					experiment = QE_TECHNIQUES[i];
					IOHelper.deletePreviousResults();
					processAllQuestions(questionsList, QE_TECHNIQUES[i]);
					Evaluation.evaluateResults(experiment,evalType);

					System.out.println("Done: " + experiment);
				}
			} else {

				experiment = COMBINATION_TECHNIQUE.toString() + "-" + String.join("+", QE_TECHNIQUES);;

				if(COMBINATION_TECHNIQUE == Combination.Linear) experiment += " (" + LINEAR_ALPHA + ")";

				IOHelper.deletePreviousResults();
				processAllQuestions(questionsList, null);
				Evaluation.evaluateResults(experiment,evalType);

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

	/**
	 * @param question
	 * @param qeTechnique
	 * @param counter
	 * @throws Exception
	 */
	private static void processQuestion(Question question, String qeTechnique, int counter) throws Exception {

		SolrHelper solrHelper = new SolrHelper();
		String relevantTerms = "";
		List<String> queryWordsList = StringHelper.stringTokenizer(question.getQuestion());
		String queryWords = String.join(" ", queryWordsList);
		Question tempQ = new Question();
		tempQ.setTopicId(question.getTopicId());
		tempQ.setQuestion(queryWords);

		if(GLOBAL_QE_ENABLED) {
			queryWords = GlobalQueryExpansion.getTranslatedQuery(queryWords);
		}

		if(!COMBINATION_ENABLED) {
			if(!qeTechnique.toLowerCase().equals("baseline")) {
				String qeClass = "pk.edu.kics.dsl.qa.qe." + qeTechnique;
				QueryExpansion qe = (QueryExpansion) Class.forName(qeClass).newInstance();
				Map<String, Double> sortedTerms = qe.getRelevantTerms(tempQ);

				if(SEMANTIC_FILTERING_ENABLED) {
					relevantTerms = SimilarityHelper.applySemanticFiltering(
							sortedTerms, queryWordsList, SEMANTIC_SOURCE_TECHNIQUE);
				} else {
					relevantTerms =  CollectionHelper.getTopTerms(sortedTerms, TOP_TERMS_TO_SELECT);
				}

				if(relevantTerms!=null && !relevantTerms.isEmpty()) {
					queryWords = StringHelper.mergeTerms(queryWords, relevantTerms);
				}
			}	
		} else {
			ArrayList<String> terms = new ArrayList<>();
			ArrayList<Map<String, Double>> lists = new ArrayList<>();

			for (int i = 0; i < QE_TECHNIQUES.length; i++) {
				String technique = QE_TECHNIQUES[i];
				String qeClass = "pk.edu.kics.dsl.qa.qe." + technique;
				QueryExpansion qe = (QueryExpansion) Class.forName(qeClass).newInstance();
				Map<String, Double> sortedTerms = qe.getRelevantTerms(tempQ);
				lists.add(sortedTerms);
				relevantTerms=  CollectionHelper.getTopTerms(sortedTerms, TOP_TERMS_TO_SELECT);
				terms.add(relevantTerms);
			}

			if(COMBINATION_TECHNIQUE == Combination.Intersection) {
				relevantTerms = CombHelper.intersect(terms);
			} else if(COMBINATION_TECHNIQUE == Combination.Linear) {
				Map<String, Double> sortedTerms = CombHelper.linear(lists, LINEAR_ALPHA);
				if(SEMANTIC_FILTERING_ENABLED) {
					relevantTerms = SimilarityHelper.applySemanticFiltering(
							sortedTerms, queryWordsList, SEMANTIC_SOURCE_TECHNIQUE);
				} else {
					relevantTerms =  CollectionHelper.getTopTerms(sortedTerms, TOP_TERMS_TO_SELECT);
				}
			} else if(COMBINATION_TECHNIQUE == Combination.Borda) {
				Map<String, Double> sortedTerms = CombHelper.borda(terms);
				if(SEMANTIC_FILTERING_ENABLED) {
					relevantTerms = SimilarityHelper.applySemanticFiltering(
							sortedTerms, queryWordsList, SEMANTIC_SOURCE_TECHNIQUE);
				} else {
					relevantTerms =  CollectionHelper.getTopTerms(sortedTerms, TOP_TERMS_TO_SELECT);
				}
			}

			queryWords = StringHelper.mergeTerms(queryWords, relevantTerms);
		}

		if(DISPLAY_RESULTS) {
			System.out.println("Final Query: " + queryWords);
		}

		// update the question with the new querywords
		tempQ.setQuestion(queryWords);

		// There are a maximum of ~614 documents for any particular topic
		ArrayList<SolrResult> resultsList = solrHelper.submitQuery(tempQ, 0, 650);
			IOHelper.writeResult(resultsList, counter,evalType,question);
	}


}
package pk.edu.kics.dsl.qa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONException;
import org.json.simple.parser.ParseException;

import com.google.common.annotations.GwtIncompatible;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.entity.SolrResult;
import pk.edu.kics.dsl.qa.qe.ACC2;
import pk.edu.kics.dsl.qa.qe.ChiSquare;
import pk.edu.kics.dsl.qa.qe.ChiSquareProbabilityBased;
import pk.edu.kics.dsl.qa.qe.GeneIndex;
import pk.edu.kics.dsl.qa.qe.GlobalQueryExpansion;
import pk.edu.kics.dsl.qa.qe.KLDivergence;
import pk.edu.kics.dsl.qa.qe.KLDivergence2;
import pk.edu.kics.dsl.qa.qe.MFT2;
import pk.edu.kics.dsl.qa.qe.PoisonRatio;
import pk.edu.kics.dsl.qa.qe.QueryExpansion;
import pk.edu.kics.dsl.qa.util.CollectionHelper;
import pk.edu.kics.dsl.qa.util.CombHelper;
import pk.edu.kics.dsl.qa.util.Evaluation;
import pk.edu.kics.dsl.qa.util.IOHelper;
import pk.edu.kics.dsl.qa.util.SimilarityHelper;
import pk.edu.kics.dsl.qa.util.SolrHelper2;
import pk.edu.kics.dsl.qa.util.SolrHelper;
import pk.edu.kics.dsl.qa.util.StringHelper;

public class BiomedQA {

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
	//private final static String[] QE_TECHNIQUES = {"ChiSquareProbabilityBased","ChiSquare","KLDivergence2","RSV2","CoDice","IG","LRF","MFT","PRF", "Rocchio","GeneIndex"};//"ChiSquareProbabilityBased","ChiSquare","KLDivergence2","RSV2","CoDice","IG","LRF", "MFT", 
	private final static String[] QE_TECHNIQUES = {"ChiSquareProbabilityBased","ChiSquare"};
	public final static int []DOCUMENTS_FOR_QE = {10};
	public final static int[] TOP_TERMS_TO_SELECT = {10};
	public final static boolean DISPLAY_RESULTS = true;

	public final static boolean STEMMING_ENABLED = false;

	public final static boolean GLOBAL_QE_ENABLED =false;

	// only applicable for individual feature selection technique - not for combinations
	public final static SemanticSource SEMANTIC_SOURCE_TECHNIQUE = SemanticSource.MeSH;
	public final static boolean SEMANTIC_FILTERING_ENABLED = false; //false
	public final static int TOP_TERMS_FOR_SEMANTIC_FILTERING = 1;

	private final static String QUESTIONS_PATH ="resources/2007topics.txt";//"resources/queries-ohsu.txt";
	public final static String SOLR_SERVER ="10.11.10.207";//"10.11.10.202";
	public final static String SOLR_CORE ="genomic_html";//"ohsumed";//"ohsumed";//"oshumed";//"genomic_html";
	public final static String CONTENT_FIELD ="body"; //"body";//"contents";	
	public final static int TOTAL_DOCUMENTS =162259;//162259;//348566;


	public static void main(String[] args) throws IOException, SolrServerException, ParseException, JSONException {

		
		
		for(int docCount=0;docCount<DOCUMENTS_FOR_QE.length;docCount++) {
			
			for(int z=0;z<TOP_TERMS_TO_SELECT.length;z++) {
			ArrayList<Question> questionsList = IOHelper.ReadQuestions(QUESTIONS_PATH);

		try {
			String experiment = "";

			if(!COMBINATION_ENABLED) {
				for (int i = 0; i < QE_TECHNIQUES.length; i++) {
					experiment = QE_TECHNIQUES[i];
					IOHelper.deletePreviousResults();
					processAllQuestions(questionsList, QE_TECHNIQUES[i],z,docCount);
					
					
					
					Evaluation.evaluateResults(experiment,TOP_TERMS_TO_SELECT[z],DOCUMENTS_FOR_QE[docCount]);

					System.out.println("Done: " + experiment);
				}
			} else {

				experiment = COMBINATION_TECHNIQUE.toString() + "-" + String.join("+", QE_TECHNIQUES);;

				if(COMBINATION_TECHNIQUE == Combination.Linear) experiment += " (" + LINEAR_ALPHA + ")";

				IOHelper.deletePreviousResults();
				processAllQuestions(questionsList, null,z,docCount);
				Evaluation.evaluateResults(experiment,TOP_TERMS_TO_SELECT[z],DOCUMENTS_FOR_QE[docCount]);

				System.out.println("Done: " + experiment);
			}

		}	
		catch (Exception e) {
			e.printStackTrace();
		}
	
	 }
		
		
		
		
		
		
		
		
	}//outer for loop
	
	
}

	private static void processAllQuestions(ArrayList<Question> questionsList, String qeTechnique,int z,int docCount) throws Exception {
		int counter = 1;
		int cc=0;
		for (Question question : questionsList) {
			System.out.println("ForQuery="+counter);
			cc++;
			processQuestion(question, qeTechnique, counter++,z,docCount);
			
			
		   /*if(cc==2)
				break;*/
			
		}
	}

	private static void processQuestion(Question question, String qeTechnique, int counter,int z,int docCount) throws Exception {

		SolrHelper2 solrHelper = new SolrHelper2();
		//SolrHelper solrHelper = new SolrHelper();
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
				Map<String, Double> sortedTerms = qe.getRelevantTerms(tempQ,docCount);

				if(SEMANTIC_FILTERING_ENABLED) {
					relevantTerms = SimilarityHelper.applySemanticFiltering(
							sortedTerms, queryWordsList, SEMANTIC_SOURCE_TECHNIQUE,z);
				} else {
					relevantTerms =  CollectionHelper.getTopTerms(sortedTerms, TOP_TERMS_TO_SELECT[z]);
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
				Map<String, Double> sortedTerms = qe.getRelevantTerms(tempQ,docCount);
				lists.add(sortedTerms);
				relevantTerms=  CollectionHelper.getTopTerms(sortedTerms, TOP_TERMS_TO_SELECT[z]);
				terms.add(relevantTerms);
			}

			if(COMBINATION_TECHNIQUE == Combination.Intersection) {
				relevantTerms = CombHelper.intersect(terms);
			} else if(COMBINATION_TECHNIQUE == Combination.Linear) {
				Map<String, Double> sortedTerms = CombHelper.linear(lists, LINEAR_ALPHA);
				if(SEMANTIC_FILTERING_ENABLED) {
					relevantTerms = SimilarityHelper.applySemanticFiltering(
							sortedTerms, queryWordsList, SEMANTIC_SOURCE_TECHNIQUE,z);
				} else {
					relevantTerms =  CollectionHelper.getTopTerms(sortedTerms, TOP_TERMS_TO_SELECT[z]);
				}
			} else if(COMBINATION_TECHNIQUE == Combination.Borda) {
				Map<String, Double> sortedTerms = CombHelper.borda(terms,z);
				if(SEMANTIC_FILTERING_ENABLED) {
					relevantTerms = SimilarityHelper.applySemanticFiltering(
							sortedTerms, queryWordsList, SEMANTIC_SOURCE_TECHNIQUE,z);
				} else {
					relevantTerms =  CollectionHelper.getTopTerms(sortedTerms, TOP_TERMS_TO_SELECT[z]);
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
		ArrayList<SolrResult> resultsList = solrHelper.submitQuery(tempQ, 0,1000);//9500
		IOHelper.writeResult(resultsList, counter);
	}


}
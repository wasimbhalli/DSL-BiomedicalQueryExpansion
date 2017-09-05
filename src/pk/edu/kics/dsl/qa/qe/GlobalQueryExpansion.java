package pk.edu.kics.dsl.qa.qe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.json.simple.parser.ParseException;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.services.MetaMapServiceCall;
import pk.edu.kics.dsl.qa.util.MetaMapResults;

public class GlobalQueryExpansion extends QueryExpansion {

	@Override
	public String getRelevantTerms(Question question, int termsToSelect) {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<String> getMetamapSynonyms(String question) {
		MetaMapServiceCall metaMapServiceCall = new MetaMapServiceCall();
		MetaMapResults resultParser;
		ArrayList<String> similarKeywords = new ArrayList<>();

		try {
			resultParser = new MetaMapResults(metaMapServiceCall.getSimilarWords(question));
			HashMap<String, Object> resultsList = resultParser.getMetaMapResult();
			similarKeywords = resultParser.getKeywords(resultsList);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return similarKeywords;
	}


	public ArrayList<String> getWordnetSynonyms(String question)
	{

		ArrayList<String> finalSynonymsList = null;
		//  Get the synsets containing the word form=capicity

		File f=new File("data/WordNet-3.0/dict");
		System.setProperty("wordnet.database.dir", f.toString());
		//setting path for the WordNet Directory

		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] synsets = database.getSynsets(question);
		//  Display the word forms and definitions for synsets retrieved

		if (synsets.length > 0){
			finalSynonymsList = new ArrayList<String>();
			// add elements to al, including duplicates
			HashSet synonymsHashlist = new HashSet();
			for (int i = 0; i < synsets.length; i++){
				String[] wordForms = synsets[i].getWordForms();
				for (int j = 0; j < wordForms.length; j++)
				{
					finalSynonymsList.add(wordForms[j]);
				}


				//removing duplicates
				synonymsHashlist.addAll(finalSynonymsList);
				finalSynonymsList.clear();
				finalSynonymsList.addAll(synonymsHashlist);

			}
		}
		return finalSynonymsList;



	}
}

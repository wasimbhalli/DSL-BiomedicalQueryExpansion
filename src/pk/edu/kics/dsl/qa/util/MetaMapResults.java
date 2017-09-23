package pk.edu.kics.dsl.qa.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.collect.ArrayListMultimap;

import pk.edu.kics.dsl.qa.entity.MetaMapCandidate;

public class MetaMapResults {
	MetaMapCandidate allcandidate;
	JSONArray allDocuments;
	HashMap<String, Object> objects;
	ArrayList<String> prefered_name;
	ArrayList<String> semantic_type;
	String phraseText;
	HashMap<String, String> symenticTypesHashMap;
	ArrayList<String> candidateCui;

	// constructor with JSONArray paramater
	public MetaMapResults(String doc) throws IOException, ParseException {
		// load abbrvation and their full name
		symenticTypesHashMap = symenticType("resources/Mappings/symenticTypes2013A.txt");
		// get the results and parse the documents phrases
		JSONParser jsonParser = new JSONParser();
		JSONObject parsedResult = (JSONObject) jsonParser.parse(new StringReader(doc));
		allDocuments = (JSONArray) parsedResult.get("AllDocuments");

		objects = new HashMap<>();
	}

	public HashMap<String, Object> getMetaMapResult() {

		JSONArray utterancesJsonArray = new JSONArray();
		new JSONObject();
		for (int i = 0; i < allDocuments.size(); i++) {
			JSONObject utterancesSingleObject = new JSONObject();
			JSONArray phrases = new JSONArray();
			utterancesJsonArray = (JSONArray) ((JSONObject) ((JSONObject) allDocuments.get(i)).get("Document"))
					.get("Utterances");
			for (int j = 0; j < utterancesJsonArray.size(); j++) {
				utterancesSingleObject = (JSONObject) utterancesJsonArray.get(j);
				phrases = (JSONArray) utterancesSingleObject.get("Phrases");
				for (int k = 0; k < phrases.size(); k++) {
					phraseText = (String) ((JSONObject) phrases.get(k)).get("PhraseText");
					// parseEach phrase array and get the components
					allcandidate = parseEachPhrase((JSONArray) ((JSONObject) ((JSONObject) phrases.get(k))).get("Mappings"));
					// add to MetaMapCandidate class object list
					// allCandidateData.add(allcandidate);
					// add to the hashmap
					objects.put(phraseText, allcandidate);
				}

			}
		}
		return objects;

	}

	// return all candidate for each phrases
	MetaMapCandidate parseEachPhrase(JSONArray mapping) {

		JSONArray mappingCandidateArray;
		JSONArray semTypesJsonArray;
		String candidatePreferred, semTypes;
		MetaMapCandidate candidate = new MetaMapCandidate();
		prefered_name = new ArrayList<>();
		semantic_type = new ArrayList<>();
		candidateCui = new ArrayList<>();
		int resource_id = 0;

		for (int i = 0; i < mapping.size(); i++) {
			resource_id = Math.abs(Integer.parseInt((String) ((JSONObject) mapping.get(i)).get("MappingScore")));
			mappingCandidateArray = (JSONArray) ((JSONObject) ((JSONObject) mapping.get(i))).get("MappingCandidates");
			for (int j = 0; j < mappingCandidateArray.size(); j++) {

				candidateCui.add((String) ((JSONObject) mappingCandidateArray.get(j)).get("CandidateCUI"));
				candidatePreferred = (String) ((JSONObject) mappingCandidateArray.get(j)).get("CandidatePreferred");
				semTypesJsonArray = (JSONArray) ((JSONObject) mappingCandidateArray.get(j)).get("SemTypes");
				// add to the prefered_name list
				prefered_name.add(candidatePreferred);
				// get the semtypes array
				for (int k = 0; k < semTypesJsonArray.size(); k++) {
					semTypes = symenticTypesHashMap.get((String) (semTypesJsonArray.get(k)));
					// add to the semantic_type list
					semantic_type.add(semTypes);

				}
			}
		}

		candidate.setResource_id(resource_id);
		candidate.setPrefered_name(prefered_name);
		candidate.setSemantic_type(semantic_type);
		candidate.setCandidateCUI(candidateCui);
		return candidate;
	}

	@SuppressWarnings("resource")
	private HashMap<String, String> symenticType(String path) throws IOException {
		FileReader file = new FileReader(path);
		BufferedReader br = new BufferedReader(file);

		String line = "";
		HashMap<String, String> symenticTypes = new HashMap<>();

		while ((line = br.readLine()) != null) {
			String[] lineSplit = line.split("\\|");
			symenticTypes.put(lineSplit[0], lineSplit[2]);
		}
		return symenticTypes;
	}

	public void getPhrases(HashMap<String, Object> metaMapResults) {

		for (Entry<String, Object> entry : metaMapResults.entrySet()) {
			String key = entry.getKey();
			System.out.println(key);
			System.out.println(((MetaMapCandidate) entry.getValue()).getPrefered_name());
			System.out.println(((MetaMapCandidate) entry.getValue()).getSemantic_type());
			System.out.println("\n\n\n");
		}

	}

	public ArrayList<String> getKeywords(HashMap<String, Object> metaMapResults) {
		ArrayList<String> keywords = new ArrayList<>();
		for (Entry<String, Object> entry : metaMapResults.entrySet()) {

			// entry.getKey();
			ArrayList<String> list = ((MetaMapCandidate) entry.getValue()).getPrefered_name();

			if (list.size() > 0) {
				for (String string : list) {
					if (keywords.contains(string))
						continue;
					keywords.add(string);

				}
			} else {
				keywords.add(entry.getKey().replace(",", ""));
			}
		}
		return keywords;

	}
	public ArrayList<String> getQuestionCui(HashMap<String, Object> metaMapResults)
	{
		ArrayList<String> questionCui =new ArrayList<>();
		for (Entry<String, Object> entry : metaMapResults.entrySet()) {
			for (String cui : ((MetaMapCandidate) entry.getValue()).getCandidateCUI()) 
			{
				questionCui.add(cui);
			}
		}	

		return questionCui;
	}

	public static LinkedHashMap<String, List<String>> ParseMetamapVariantsReponse(String Jsonstr) {

		Matcher match1=Pattern.compile("\\bAllDocuments\\b").matcher(Jsonstr);
		Matcher match2=Pattern.compile("\\bDocument\\b").matcher(Jsonstr);

		if(match1.find())
		{
			String []getSynList=Jsonstr.split("\\bAllDocuments\\b");
			Jsonstr=getSynList[1];
		}
		if(match2.find())
		{
			String []getSynList=Jsonstr.split("\\bDocument\\b");
			Jsonstr=getSynList[0];
		}

		return getSynRomGene(Jsonstr);
	}

	public static LinkedHashMap<String, List<String>> getSynRomGene(String output) {
		ArrayList<Integer>variantList=new ArrayList<Integer>();
		ArrayList<String>importantWords=new ArrayList<String>();
		ArrayList<String>variantCountForTerm=new ArrayList<String>();
		ArrayList<String> finalResult=new ArrayList<String>();

		ArrayList<String> resultLines = new ArrayList<String>();
		LinkedHashMap<String, List<String>> map=new LinkedHashMap<String, List<String>>();

		String[] lines = output.split(System.getProperty("line.separator"));

		try {
			for (int j = 0; j < lines.length; j++) {
				int k = j;
				if (lines[k].contains("variants"))
				{   
					variantCountForTerm.add(lines[k]);
					while (!(lines[k + 1].contains("variants"))) {
						String line = lines[k + 1];
						resultLines.add(line);
						k++;
					}
				}
			}
		} catch (Exception n)  
		{
			n.getMessage();
		}

		for(int i=0;i<resultLines.size();i++)
		{  
			try 
			{	if(!resultLines.get(i).equals("")) {
				String str = resultLines.get(i).substring(resultLines.get(i).indexOf(' '), resultLines.get(i).indexOf('{')).trim();
				finalResult.add(str);
			}
			}catch (Exception e) {
				System.out.println("");
			}

		}

		for(int i=0;i<variantCountForTerm.size();i++)
		{   String countString=variantCountForTerm.get(i).substring(variantCountForTerm.get(i).indexOf('='),variantCountForTerm.get(i).indexOf(')'));

		int num=0;
		StringBuilder st=new StringBuilder();

		for(int ii=1;ii<countString.length();ii++)
		{    st.append(String.valueOf(countString.charAt(ii)));  //Character.digit(countString.charAt(1), 1000);

		}
		num=Integer.parseInt(st.toString());
		variantList.add(num);

		String impWord=variantCountForTerm.get(i).substring(0,variantCountForTerm.get(i).indexOf('['));
		importantWords.add(impWord);
		}

		int k=0;
		for(int j=0;j<importantWords.size();j++)
		{	
			List<String>temp= new ArrayList<String>();	    
			for(int i=0;i<variantList.get(j);i++)
			{	      
				temp.add(finalResult.get(k));

				k++;			    
			} 	    
			map.put(importantWords.get(j), temp);
		}

		return map;
	}
}
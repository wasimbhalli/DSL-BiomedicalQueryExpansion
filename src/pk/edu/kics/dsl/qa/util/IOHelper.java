package pk.edu.kics.dsl.qa.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import pk.edu.kics.dsl.qa.BiomedQA;
import pk.edu.kics.dsl.qa.BiomedQA.EvaluationType;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.entity.SolrResult;

public class IOHelper {

	static JSONArray results = new JSONArray();
	// TODO: Set all paths to refer to local resources folder
	final static String docResult = "resources/queryResults.txt";

	public static ArrayList<Question> ReadQuestions(String path, EvaluationType type) throws IOException {
		ArrayList<Question> questionList = new ArrayList<>();
		Question questionEntity;
		BufferedReader br = null;
		FileReader fr = null;
		fr = new FileReader(new File(path));
		br = new BufferedReader(fr);
		String sCurrentLine;
		if (type == EvaluationType.TRECGenomic) {
			// TODO: read question for trecGenomic
			while ((sCurrentLine = br.readLine()) != null) {
				questionEntity = new Question();
				questionEntity.topicId = sCurrentLine.substring(1, sCurrentLine.indexOf(">"));
				questionEntity.MeSHAspects = sCurrentLine.substring(sCurrentLine.indexOf("[") + 1,
						sCurrentLine.indexOf("]"));
				questionEntity.text = sCurrentLine.substring(sCurrentLine.indexOf(">") + 1, sCurrentLine.indexOf("["))
						+ questionEntity.MeSHAspects.toLowerCase()
						+ sCurrentLine.substring(sCurrentLine.indexOf("]") + 1, sCurrentLine.indexOf("?"));
				questionList.add(questionEntity);
			}
			br.close();
			fr.close();

		} else if (type == EvaluationType.BIOAsq) {
			String id = null, body = null, qtype = null;
			try {
				FileReader reader = new FileReader(path);
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

				// check the file for illegal characters
				String s = jsonObject.toString();
				if (s.contentEquals("\\\"") & s.contentEquals("]\"")) {
					String s2 = s.replace("\"[", "[").replace("]\"", "]").replace("\\\"", "\"");
					jsonObject = (JSONObject) jsonParser.parse(s2);
				}

				JSONArray questionsArray = (JSONArray) jsonObject.get("questions");

				// Parse the question file ..
				for (int i = 0; i < questionsArray.size(); i++) {
					JSONObject questionObject = (JSONObject) questionsArray.get(i);

					if (questionObject.get("id") != null) {
						id = questionObject.get("id").toString();
					}
					if (questionObject.get("body") != null) {
						body = (String) questionObject.get("body");
					}
					if (questionObject.get("type") != null) {
						qtype = (String) questionObject.get("type");

					}

					Question item = new Question();
					item.setQuestion(body);
					item.setTopicId(id);
					item.setType(qtype);
					questionList.add(item);
				}
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (NullPointerException ex) {
				ex.printStackTrace();
			}
		}

		return questionList;
	}

	// TODO: Append results of all questions in a single file
	@SuppressWarnings("unchecked")
	public static void writeResult(ArrayList<SolrResult> resultsList, int questionNo, EvaluationType type,
			Question question) throws IOException {
		
		BufferedWriter writer;
		File f = new File(docResult);
		if (f.exists()) {
			// System.out.println("File existed");
			writer = new BufferedWriter(new FileWriter(docResult, true));
		} else {
			writer = new BufferedWriter(new FileWriter(docResult));
		}
		// new PrintWriter();
		// String []lines = resultFile.toString().split(System.lineSeparator());
		if (type == EvaluationType.TRECGenomic) {
			writeTRECGEnomicResult(results, writer);
		} else if (type == EvaluationType.BIOAsq) {
			writeBIOAsqResults(resultsList,writer,question);
		}

	}

	public static ArrayList<String> getListFromTextFile(String path) {
		ArrayList<String> linesList = new ArrayList<>();
		BufferedReader br = null;
		FileReader fr = null;

		try {
			fr = new FileReader(new File(path));
			br = new BufferedReader(fr);
			String line;

			while ((line = br.readLine()) != null) {
				linesList.add(line);
			}

			br.close();
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return linesList;
	}

	public static void deletePreviousResults() {
		File f = new File(docResult);
		f.delete();
	}

	// json format result of the BIO-ASQ
	@SuppressWarnings("unchecked")
	public static void writeSubmissionFile(JSONArray results, String name) {

		JSONObject resultObject = new JSONObject();
		resultObject.put("username", "wasim");
		resultObject.put("password", "wasim");
		resultObject.put("system", "TestSystem");
		resultObject.put("questions", results);

		System.out.println(resultObject.toJSONString());

		FileWriter file = null;
		try {
			String path = name.substring(0,name.lastIndexOf(".")) + ".json";
			file = new FileWriter(path);
			file.write(resultObject.toString());
			System.out.println("File Succussfully saved in Resources");
			
		} catch (IOException r) {
			r.getMessage();
		} finally {

			try {
				file.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings({ "null", "unused" })
	private static void writeTRECGEnomicResult(ArrayList<SolrResult> resultsList, BufferedWriter writer)
			throws IOException {

		File f = new File(docResult);
		for (SolrResult result : resultsList) {
			String resultLine = result.getTopicId() + "\t" + result.getPmid() + "\t" + result.getRank() + "\t"
					+ result.getStartOffset() + "\t" + result.getLength() + "\n";

			writer.write(resultLine);

		}

		writer.flush();
		writer.close();

	}

	@SuppressWarnings({ "unchecked", "unused" })
	private static void writeBIOAsqResults(ArrayList<SolrResult> resultsList, BufferedWriter writer,
			Question question) {
		JSONArray documentURLs = null;
		JSONObject questionResult;

		questionResult = new JSONObject();
		documentURLs = new JSONArray();
		JSONArray concepts, documents, snippets, triplets;
		System.out.println("Retrieving Documents ...");
		for (SolrResult result : resultsList) {

			documentURLs.add("http://www.ncbi.nlm.nih.gov/pubmed/" + result.getPmid());
		}
		questionResult.put("documents", documentURLs);
		questionResult.put("id", question.getTopicId());
		questionResult.put("concepts", new JSONArray());
		questionResult.put("snippets", new JSONArray());
		questionResult.put("triplets", new JSONArray());

		results.add(questionResult);
		if (--BiomedQA.TOTAL_QUESTION == 0) {
			writeSubmissionFile(results, docResult);
			results.clear();

		}
	}

}
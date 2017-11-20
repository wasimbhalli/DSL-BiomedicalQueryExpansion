package pk.edu.kics.dsl.qa.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.entity.SolrResult;

public class IOHelper {

	// TODO: Set all paths to refer to local resources folder
	final static String docResult = "resources/queryResults.txt";

	public static ArrayList<Question> ReadQuestions(String path) throws IOException {
		ArrayList<Question> questionList = new ArrayList<>();
		Question questionEntity;
		BufferedReader br = null;
		FileReader fr = null;
		fr = new FileReader(new File(path));
		br = new BufferedReader(fr);
		String sCurrentLine;

		while ((sCurrentLine = br.readLine()) != null) {
			questionEntity = new Question();
			questionEntity.topicId = Integer.parseInt(sCurrentLine.substring(1, sCurrentLine.indexOf(">")));
			questionEntity.MeSHAspects = sCurrentLine.substring(sCurrentLine.indexOf("[") + 1,
					sCurrentLine.indexOf("]"));
			questionEntity.text = 
					sCurrentLine.substring(sCurrentLine.indexOf(">") + 1, sCurrentLine.indexOf("[")) +
					questionEntity.MeSHAspects.toLowerCase() + 
					sCurrentLine.substring(sCurrentLine.indexOf("]") + 1, sCurrentLine.indexOf("?"));
			questionList.add(questionEntity);
		}

		br.close();
		fr.close();

		return questionList;
	}

	// TODO: Append results of all questions in a single file
	public static void writeResult(ArrayList<SolrResult> resultsList, int questionNo) throws IOException {

		BufferedWriter writer;

		File f = new File(docResult);

		if (f.exists()) {
			//System.out.println("File existed");
			writer = new BufferedWriter(new FileWriter(docResult, true));
		} else {
			writer = new BufferedWriter(new FileWriter(docResult));
		}
		// new PrintWriter();
		// String []lines = resultFile.toString().split(System.lineSeparator());
		for (SolrResult result : resultsList) {
			String resultLine = result.getTopicId() + "\t" + result.getPmid() + "\t" + result.getRank() + "\t"
					+ result.getStartOffset() + "\t" + result.getLength()+"\n";

			writer.write(resultLine);
		}

		writer.flush();
		writer.close();
		System.out.println("Done with query: " + questionNo);

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
}

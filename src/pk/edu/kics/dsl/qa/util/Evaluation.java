package pk.edu.kics.dsl.qa.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Evaluation {

	// Set all paths to refer to local resources folder
	final static String goldStdPath = "resources/trecgen2007.gold.standard.tsv.txt";
	final static String pyScript = "resources/script/trecgen2007_score.py";

	public static void evaluateResults(String experiment) throws IOException {
		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec("python" + " " + pyScript + " " + goldStdPath + " " + "resources/queryResults.txt");
		try {
			proc.waitFor();
			BufferedReader bfr = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = "";
			String resultsFileName = experiment + ".txt";

			PrintWriter pw = new PrintWriter("resources/DocResult/"+resultsFileName);
			String MAP="";
			while ((line = bfr.readLine()) != null) {
				// display each output line form python script
				System.out.println(line);
				pw.write(line + "\n");
				MAP = line.substring(line.lastIndexOf("\t"));
			}
			bfr.close();
			pw.flush();
			pw.close();
			
			File f=new File("resources/DocResult/Allresults.csv");
			BufferedWriter mapwriter; 			
			if (f.exists()) {
				//System.out.println("File existed");
				mapwriter = new BufferedWriter(new FileWriter(f, true));
			} else {
				mapwriter = new BufferedWriter(new FileWriter(f));
			}
			mapwriter.write(experiment.trim() + "," + MAP.trim() + "\n");
			mapwriter.flush();
			mapwriter.close();
			

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}

}

package pk.edu.kics.dsl.qa.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import pk.edu.kics.dsl.qa.BiomedQA.EvaluationType;
import pk.edu.kics.dsl.qa.evaluation.EvaluatorMainClass;

public class Evaluation {

	// Set all paths to refer to local resources folder
	final static String goldStdPath = "resources/trecgen2007.gold.standard.tsv.txt";
	final static String pyScript = "resources/script/trecgen2007_score.py";

	public static void evaluateResults(String experiment, EvaluationType type) throws IOException {
		
		// for python script evaluation
		if (type == EvaluationType.TRECGenomic) {
			
			BufferedReader bfr;
			String resultsFileName = experiment + ".txt";
			PrintWriter pw;
			String line = "";
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec("python" + " " + pyScript + " " + goldStdPath + " " + "resources/queryResults.txt");
			try {
				proc.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bfr = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			pw = new PrintWriter("resources/DocResult/" + resultsFileName);
			String MAP = "";
			while ((line = bfr.readLine()) != null) {
				// display each output line form python script
				System.out.println(line);
				pw.write(line + "\n");
				MAP = line.substring(line.lastIndexOf("\t"));
			}
			bfr.close();
			pw.flush();
			pw.close();

			File f = new File("resources/DocResult/Allresults.csv");
			BufferedWriter mapwriter;
			if (f.exists()) {
				// System.out.println("File existed");
				mapwriter = new BufferedWriter(new FileWriter(f, true));
			} else {
				mapwriter = new BufferedWriter(new FileWriter(f));
			}
			mapwriter.write(experiment.trim() + "," + MAP.trim() + "\n");
			mapwriter.flush();
			mapwriter.close();

		} 
		
		
		// for BIO-ASQ Golden Standard comparison  
		else if (type == EvaluationType.BIOAsq) 
		{
			EvaluatorMainClass eval;
	    	eval = new EvaluatorMainClass("resources/BioASQ-task2bPhaseA-testset1.docs.concepts.documents.gold.json", "resources\\BioASQ-task2bPhaseA-testset1.docs.concepts.multiple.json",2);
	        eval.setVERSION_OF_CHALLENGE(EvaluatorMainClass.BIOASQ2);
	        eval.EvaluatePhaseA();
			

		}

			

	
	}

}

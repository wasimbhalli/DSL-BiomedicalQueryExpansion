package pk.edu.kics.dsl.qa.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.KStemFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeFactory;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class StringHelper {

	public static ArrayList<String> stopWords = new ArrayList<>(Arrays.asList("-=","_","=","top","icons","http","(",")",".",",","a","gif","research","sec","rect","about","above","across","after","again","against","all","almost","alone","along","already","also","although","always","among","an","and","another","any","anybody","anyone","anything","anywhere","are","area","areas","around","as","ask","asked","asking","asks","at","away","b","back","backed","backing","backs","be","became","because","become","becomes","been","before","began","behind","being","beings","best","better","between","big","both","but","by","c","came","can","cannot","case","cases","certain","certainly","clear","clearly","come","could","d","did","differ","different","differently","do","does","done","down","down","downed","downing","downs","during","e","each","early","either","end","ended","ending","ends","enough","even","evenly","ever","every","everybody","everyone","everything","everywhere","f","face","faces","fact","facts","far","felt","few","find","finds","first","for","four","from","full","fully","further","furthered","furthering","furthers","g","gave","general","generally","get","gets","give","given","gives","go","going","good","goods","got","great","greater","greatest","group","grouped","grouping","groups","h","had","has","have","having","he","her","here","herself","high","high","high","higher","highest","him","himself","his","how","however","i","if","important","in","interest","interested","interesting","interests","into","is","it","its","itself","j","just","k","keep","keeps","kind","knew","know","known","knows","l","large","largely","last","later","latest","least","less","let","lets","like","likely","long","longer","longest","m","made","make","making","man","many","may","me","member","members","men","might","more","most","mostly","mr","mrs","much","must","my","myself","n","necessary","need","needed","needing","needs","never","new","new","newer","newest","next","no","nobody","non","noone","not","nothing","now","nowhere","number","numbers","o","of","off","often","old","older","oldest","on","once","one","only","open","opened","opening","opens","or","order","ordered","ordering","orders","other","others","our","out","over","p","part","parted","parting","parts","per","perhaps","place","places","point","pointed","pointing","points","possible","present","presented","presenting","presents","problem","problems","put","puts","q","quite","r","rather","really","right","right","room","rooms","s","said","same","saw","say","says","second","seconds","see","seem","seemed","seeming","seems","sees","several","shall","she","should","show","showed","showing","shows","side","sides","since","small","smaller","smallest","so","some","somebody","someone","something","somewhere","state","states","still","still","such","sure","t","take","taken","than","that","the","their","them","then","there","therefore","these","they","thing","things","think","thinks","this","those","though","thought","thoughts","three","through","thus","to","today","together","too","took","toward","turn","turned","turning","turns","two","u","under","until","up","upon","us","use","used","uses","v","very","w","want","wanted","wanting","wants","was","way","ways","we","well","wells","went","were","what","when","where","whether","which","while","who","whole","whose","why","will","with","within","without","work","worked","working","works","would","x","y","year","years","yet","you","young","younger","youngest","your","yours","z"));

	public static ArrayList<String> analyzeContent(String content, boolean simpleTokenizer) throws IOException {

		ArrayList<String> tokens = new ArrayList<>();

		if(simpleTokenizer) {
			tokens = stringTokenizer(content);
			//tokens = new ArrayList<String>(Arrays.asList(openNLPTokenizer(content)));
		} else {
			tokens = solrPreprocessor(content);
		}

		return tokens;
	}

	public static ArrayList<String> stringTokenizer(String content){
		StringTokenizer tokenizer = new StringTokenizer(content," ><@^%$!1}+-$&%*(/)0123456789#\t\n\r\f,.:;?![]'");
		ArrayList<String> contentWords = new ArrayList<String>();
		
		while (tokenizer.hasMoreTokens()) {
			String word = tokenizer.nextToken().toLowerCase().trim();
			if(!stopWords.contains(new String(word)))
				contentWords.add(word);
 		}
		
		return contentWords;
    }
	
	public static String[] openNLPTokenizer(String content) throws FileNotFoundException {

		File initialFile = new File("data/opennlp-models/en-token.bin");
		InputStream modelIn = new FileInputStream(initialFile);
		try {
			TokenizerModel model = new TokenizerModel(modelIn);
			Tokenizer tokenizer = new TokenizerME(model);
			String tokens[] = tokenizer.tokenize(content);
			return tokens;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				}
				catch (IOException e) {
				}
			} 
		} 

		return null;
	}	

	public static ArrayList<String> solrPreprocessor(String content) throws IOException {
		AttributeFactory factory = AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY;
		StandardTokenizer tokenizer = new StandardTokenizer(factory);
		ArrayList<String> tokens = new ArrayList<String>();
		tokenizer.setReader(new StringReader(content));
		tokenizer.reset();
		CharTermAttribute attr = tokenizer.addAttribute(CharTermAttribute.class);

		Map<String, String> param = new HashMap<>();
		param.put("luceneMatchVersion", "LUCENE_66");

		LowerCaseFilterFactory lowerCaseFactory = new LowerCaseFilterFactory(param);
		TokenStream tokenStream = lowerCaseFactory.create(tokenizer);

		/*KStemFilterFactory kstemFilterFactory = new KStemFilterFactory(param);
		tokenStream = kstemFilterFactory.create(tokenStream);*/

		CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
		tokenStream = new StopFilter(tokenStream, stopWords);

		while(tokenStream.incrementToken()) {
			String term = attr.toString();
			tokens.add(term);
		}

		tokenStream.close();
		return tokens;
	}

	public static Map<String , Integer> getWordsFrequency(ArrayList<String> tokens) {
		Map<String , Integer> dictionary=new HashMap<String,Integer>();
		for(String s:tokens){
			String normalizeWord = normalizeWord(s);
			if(dictionary.containsKey(normalizeWord))
				dictionary.put(normalizeWord, dictionary.get(normalizeWord) + 1 );
			else
				dictionary.put(normalizeWord, 1);
		}

		return dictionary;
	}

	public static String getTermsByComma(ArrayList<String> terms) {
		return String.join(",", terms);
	}
	
	public static String normalizeWord(String word) {
		return word.replace(",", "");
		/*return word.replaceAll(",", "").replaceAll("\"", "").replaceAll("\\{", "").
				replaceAll("\\}", "").replaceAll("\\(", "").replaceAll("\\)", "");*/
	}

	public static String normalize2(String word) {
		return word.replaceAll(",", "").replaceAll("\"", "").replaceAll("\\{", "").
				replaceAll("\\}", "").replaceAll("\\(", "").replaceAll("\\)", "").
				replaceAll(":", "");
	}
}

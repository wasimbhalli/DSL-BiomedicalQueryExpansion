package pk.edu.kics.dsl.qa.qe;

import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;
import pk.edu.kics.dsl.qa.util.QEHelper;

public class TFIDF extends LocalQueryExpansion {
	
	@Override
	public Map<String, Double> getRelevantTerms(Question question) {
		try {
			super.init(question);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		HashMap<String, Double> termsTFIDF = QEHelper.getTermsTFIDF(localDictionary, documentFrequency, documentTermFrequencies);
		
		return CollectionHelper.sortByComparator(termsTFIDF, false);
	}
}
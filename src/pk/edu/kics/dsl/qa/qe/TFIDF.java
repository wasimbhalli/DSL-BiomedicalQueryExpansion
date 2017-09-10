package pk.edu.kics.dsl.qa.qe;

import java.util.HashMap;
import java.util.Map;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;
import pk.edu.kics.dsl.qa.util.QEHelper;

public class TFIDF extends LocalQueryExpansion {
	
	@Override
	public String getRelevantTerms(Question question, int termsToSelect) {
		try {
			super.init(question);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		HashMap<String, Double> termsTFIDF = QEHelper.getTermsTFIDF(localDictionary, documentFrequency, documentTermFrequencies);
		
		Map<String, Double> sortedTermsTFIDF = CollectionHelper.sortByComparator(termsTFIDF, false);
		return CollectionHelper.getTopTerms(sortedTermsTFIDF, termsToSelect);
	}
}
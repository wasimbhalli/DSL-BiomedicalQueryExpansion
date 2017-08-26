package pk.edu.kics.dsl.qa.qe;

import java.util.Map;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class MFT extends LocalQueryExpansion {

	@Override
	public String getRelevantTerms(Question question, int termsToSelect) {
		try {
			super.init(question);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Map<String,Integer> terms = CollectionHelper.sortByComparatorInt(localTermsTotalFrequency, false); 
		return CollectionHelper.getTopTerms(terms, termsToSelect);
	}

}

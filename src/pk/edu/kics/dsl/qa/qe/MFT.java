package pk.edu.kics.dsl.qa.qe;

import java.util.Map;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class MFT extends LocalQueryExpansion {

	@Override
	public Map<String, Double> getRelevantTerms(Question question) {
		try {
			super.init(question);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return CollectionHelper.sortByComparatorInt(localTermsTotalFrequency, false); 
	}

}

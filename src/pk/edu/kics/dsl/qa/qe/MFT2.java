package pk.edu.kics.dsl.qa.qe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.CollectionHelper;

public class MFT2 extends LocalQueryExpansion {


	
	@Override
	public Map<String, Double> getRelevantTerms(Question question,int docCount) {
		try {
			super.init(question,docCount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	
		return CollectionHelper.sortByComparatorInt(localTermsTotalFrequency, false); 
	
	
	
	
	
	}

	
}//class

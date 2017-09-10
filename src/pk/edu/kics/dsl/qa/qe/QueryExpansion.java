package pk.edu.kics.dsl.qa.qe;

import pk.edu.kics.dsl.qa.entity.Question;
import pk.edu.kics.dsl.qa.util.StringHelper;

public abstract class QueryExpansion {

	public abstract String getRelevantTerms(Question question, int termsToSelect);
	
	public String mergeTerms(String actualTerms, String newTerms) {
		return actualTerms + " " + StringHelper.removeSOLRSymbols(newTerms);
	}
	
}

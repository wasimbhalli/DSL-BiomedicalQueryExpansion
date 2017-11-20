package pk.edu.kics.dsl.qa.qe;

import java.util.Map;

import pk.edu.kics.dsl.qa.entity.Question;

public abstract class QueryExpansion {

	public abstract Map<String, Double> getRelevantTerms(Question question);
	
}

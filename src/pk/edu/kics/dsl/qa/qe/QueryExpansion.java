package pk.edu.kics.dsl.qa.qe;

import java.util.ArrayList;
import pk.edu.kics.dsl.qa.entity.Question;

public abstract class QueryExpansion {
	 public abstract String getRelevantTerms(Question question);
	 
	 public String mergeTerms(String actualTerms, String newTerms) {
		 return actualTerms + " " + newTerms;
	 }
}

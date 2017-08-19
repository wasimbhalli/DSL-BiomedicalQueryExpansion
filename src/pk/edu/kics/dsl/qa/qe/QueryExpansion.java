package pk.edu.kics.dsl.qa.qe;

import pk.edu.kics.dsl.qa.entity.Question;

public abstract class QueryExpansion {
	 public abstract String getRelevantTerms(Question question);
	 
	 public String mergeTerms(String actualTerms, String newTerms) {
		 return actualTerms + " " + newTerms;
	 }
	 public void getMetamapSynonyms(String question) 
	 {
	 }

}

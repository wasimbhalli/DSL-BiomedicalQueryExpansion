package pk.edu.kics.dsl.qa.entity;

public class Question {
	
	public int topicId;
	public String text;
	public String MeSHAspects;
	
	public String getQuestion() {
		return text;
	}

	public void setQuestion(String question) {
		this.text = question;
	}

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topId) {
		this.topicId = topId;
	}

	public String getMeSHAspects() {
		return MeSHAspects;
	}

	public void setMeSHAspects(String meSHaspects) {
		MeSHAspects = meSHaspects;
	}

	public Question() {
	}

}

package pk.edu.kics.dsl.qa.entity;

public class Question {
	
	public String topicId;
	public String text;
	public String type;
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String MeSHAspects;
	
	public String getQuestion() {
		return text;
	}

	public void setQuestion(String question) {
		this.text = question;
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topId) {
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

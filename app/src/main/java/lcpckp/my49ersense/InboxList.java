package lcpckp.my49ersense;

public class InboxList {
	private String user, questionText, domain, questionImage,answerText, answerImage, answeredBy;

	
	public String getUser() {
		return user;
	}


	public void setUser(String user) {
		this.user = user;
	}


	public String getQuestionText() {
		return questionText;
	}


	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}


	public String getDomain() {
		return domain;
	}


	public void setDomain(String domain) {
		this.domain = domain;
	}


	public String getQuestionImage() {
		return questionImage;
	}


	public void setQuestionImage(String questionImage) {
		this.questionImage = questionImage;
	}


	public String getAnswerText() {
		return answerText;
	}


	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}


	public String getAnswerImage() {
		return answerImage;
	}


	public void setAnswerImage(String answerImage) {
		this.answerImage = answerImage;
	}


	public String getAnsweredBy() {
		return answeredBy;
	}


	public void setAnsweredBy(String answeredBy) {
		this.answeredBy = answeredBy;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.user + "        \t     \t \t         \t         on " + this.domain+"\n\n"+this.questionText;
	}

}

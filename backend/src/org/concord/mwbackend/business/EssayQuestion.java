package org.concord.mwbackend.business;

public class EssayQuestion {

	private String question;

	private String answer;

	private String referenceAnswer;

	private String address;

	private int id;

	private long timestamp = -1;

	public EssayQuestion() {
	}

	public void setID(int i) {
		id = i;
	}

	public int getID() {
		return id;
	}

	public void setTimestamp(long t) {
		timestamp = t;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setAddress(String s) {
		address = s;
	}

	public String getAddress() {
		return address;
	}

	public void setQuestion(String s) {
		question = s;
	}

	public String getQuestion() {
		return question;
	}

	public void setAnswer(String s) {
		answer = s;
	}

	public String getAnswer() {
		return answer;
	}

	public void setReferenceAnswer(String s) {
		referenceAnswer = s;
	}

	public String getReferenceAnswer() {
		return referenceAnswer;
	}

}